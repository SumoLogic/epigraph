/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.sumologic.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.immutable.ListMap
import scala.collection.mutable


trait CType {self =>

  type This >: this.type <: CType {type This <: self.This}

  val name: CTypeName

  val kind: CTypeKind

  // `None` - no declaration, `Some(None)` - declared nodefault, `Some(Some(String))` - declared default
  val declaredDefaultTagName: Option[Option[String]]

  //def effectiveTagsMap: Map[String, CTag]

  def isAssignableFrom(subtype: CType): Boolean

  def linearizedParents: Seq[This]

  /** Immediate parents of this type in order of increasing priority */
  def getLinearizedParentsReversed: java.lang.Iterable[This] = linearizedParents.reverse // TODO phase guard?

}


abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit val ctx: CContext) extends CType {self =>

  override type This >: this.type <: CTypeDef {type This <: self.This}

  @scala.beans.BeanProperty
  val name: CTypeFqn = new CTypeFqn(csf, csf.namespace.fqn, psi)

  val isAbstract: Boolean = psi.getAbstract != null

  val isPolymorphic: Boolean = psi.getPolymorphic != null

  val declaredSupertypeRefs: Seq[CTypeDefRef] = {
    @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
    if (sed == null) Nil else sed.getFqnTypeRefList.map(CTypeRef(csf, _))
  }

  def declaredParents: Seq[CTypeDef] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, cachedDeclaredParents)

  private lazy val cachedDeclaredParents: Seq[CTypeDef] = declaredSupertypeRefs.map(_.resolved)

  val declaredSupplementees: Seq[CTypeDefRef] = {
    @Nullable val ssd: SchemaSupplementsDecl = psi.getSupplementsDecl
    if (ssd == null) Nil else ssd.getFqnTypeRefList.map(CTypeRef(csf, _))
  }

  val injectedSupertypes: ConcurrentLinkedQueue[CTypeDef] = new ConcurrentLinkedQueue

  def declaredAndInjectedParents: Seq[CTypeDef] = ctx.after(
    CPhase.RESOLVE_TYPEREFS, null, cachedDeclaredAndInjectedParents
  )

  private lazy val cachedDeclaredAndInjectedParents: Seq[CTypeDef] = declaredParents ++ injectedSupertypes

  def supertypes: Seq[This] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, computedSupertypes.getOrElse(Nil))

  private var computedSupertypes: Option[Seq[This]] = None

  def computeSupertypes(visited: mutable.Stack[CTypeDef]): Unit = {
    if (computedSupertypes.isEmpty) {
      val thisIdx = visited.indexOf(this)
      visited.push(this)
      if (thisIdx == -1) {
        declaredAndInjectedParents foreach { p =>
          p.computeSupertypes(visited)
          if (p.kind != kind) {
            val pSource = if (injectedSupertypes.contains(p)) "injected" else "declared"
            ctx.errors.add(
              CError(
                csf.filename, csf.position(psi.getQid), // TODO use injection source/position for injection failures
                s"Type ${name.name} (of '${kind.keyword}' kind) is not compatible with $pSource supertype ${p.name.name} (of `${p.kind.keyword}` kind)"
              )
            )
          }
        }
        computedSupertypes = Some(
          declaredAndInjectedParents.flatMap { st => st.supertypes :+ st }.distinct.asInstanceOf[Seq[This]]
        )
      } else {
        ctx.errors.add(
          CError(
            csf.filename, csf.position(psi.getQid),
            s"Cyclic inheritance: type ${visited.view(0, thisIdx + 2).reverseIterator.map(_.name.name).mkString(" < ")}"
          )
        )
        computedSupertypes = Some(Nil)
      }
      visited.pop()
    }
  }

  def parents: Seq[This] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, cachedParents)

  private lazy val cachedParents: Seq[This] = (declaredSupertypeRefs.map(_.resolved) ++ injectedSupertypes)
      .asInstanceOf[Seq[This]]

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[This] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[This] = parents.foldLeft[Seq[This]](Nil) { (acc, p) =>
    if (acc.contains(p) || parents.exists(_.supertypes.contains(p))) acc else p +: acc
  }

  def linearizedSupertypes: Seq[This] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedSupertypes)

  private lazy val _linearizedSupertypes: Seq[This] = parents.foldLeft[Seq[This]](Nil) { (acc, p) =>
    p.linearized.filterNot(acc.contains) ++ acc
  }

  def linearized: Seq[This] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearized)

  private lazy val _linearized: Seq[This] = this.asInstanceOf/*scalac bug*/ [this.type] +: linearizedSupertypes

  override def isAssignableFrom(subtype: CType): Boolean = subtype match {
    case subDef: CTypeDef => kind == subtype.kind && subDef.linearized.contains(this)
    case _ => false
  }

}

object CTypeDef {

  def apply(csf: CSchemaFile, stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CTypeDef = stdw.getElement match {
    case typeDef: SchemaVarTypeDef => new CVarTypeDef(csf, typeDef)
    case typeDef: SchemaRecordTypeDef => new CRecordTypeDef(csf, typeDef)
    case typeDef: SchemaMapTypeDef => new CMapTypeDef(csf, typeDef)
    case typeDef: SchemaListTypeDef => new CListTypeDef(csf, typeDef)
    case typeDef: SchemaEnumTypeDef => new CEnumTypeDef(csf, typeDef)
    case typeDef: SchemaPrimitiveTypeDef => new CPrimitiveTypeDef(csf, typeDef)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def declaredDefaultTagName(@Nullable sdo: SchemaDefaultOverride): Option[Option[String]] = {
    if (sdo == null) {
      None
    } else {
      @Nullable val vtr = sdo.getVarTagRef
      if (vtr == null) {
        assert(sdo.getNodefault ne null)
        Some(None)
      } else {
        Some(Some(vtr.getQid.getCanonicalName))
      }
    }
  }

}


class CVarTypeDef(csf: CSchemaFile, override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.VARTYPE
) {

  override type This = CVarTypeDef

  // `None` - no declaration, `Some(None)` - declared nodefault, `Some(Some(String))` - declared default
  override val declaredDefaultTagName: Option[Option[String]] = CTypeDef.declaredDefaultTagName(psi.getDefaultOverride)

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredTagsMap: Map[String, CTag] = declaredTags.map { ct => (ct.name, ct) }(collection.breakOut)

  def effectiveTagsMap: Map[String, CTag] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveTagsMap)

  private lazy val _effectiveTagsMap = {
    linearizedParents.flatMap(_.effectiveTagsMap) ++ declaredTagsMap
    //(for {vt <- linearizedParents; nt <- vt.effectiveTagsMap} yield nt) ++ declaredTagsMap
  } groupBy { case (n, _t) =>
    n
  } map { case (n, seq) =>
    (n, seq.map { case (_n, t) => t })
  } map { case (n, seq) =>
    (n, effectiveTag(declaredTagsMap.get(n), seq))
  }

  private def effectiveTag(declaredTagOpt: Option[CTag], supertags: Seq[CTag]): CTag = {
    declaredTagOpt match {
      case Some(dt) => // check if declared tag is compatible with all (if any) overridden ones
        supertags foreach { st =>
          if (!dt.compatibleWith(st)) {
            ctx.errors.add(
              CError(
                csf.filename, csf.position(dt.psi),
                s"Type `${
                  dt.typeRef.resolved.name.name
                }` of tag `${dt.name}` is not a subtype of its parent tag type `${
                  st.typeRef.resolved.name.name
                }`"
              )
            )
          }
        }
        dt
      case None => // find the most narrow tag among inherited ones
        supertags.find(st => supertags.forall(_.typeRef.resolved.isAssignableFrom(st.typeRef.resolved))) match {
          case Some(narrowest) =>
            narrowest
          case None =>
            ctx.errors.add(
              CError(
                csf.filename, csf.position(psi.getQid), s"Multiple inherited tags `${supertags.head.name}` of types ${
                  supertags.map(_.typeRef.resolved.name.name).mkString("`", "`, `", "`")
                } must be overridden with common subtype"
              )
            )
            supertags.head // there must be at least one
        }
    }
  }

}

class CTag(val csf: CSchemaFile, val psi: SchemaVarTagDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  val overriddenTags: Seq[CTag] = Nil // FIXME

  def compatibleWith(st: CTag): Boolean = st.typeRef.resolved.isAssignableFrom(typeRef.resolved)

}

trait CDatumType extends CType {

  val ImpliedDefaultTagName: String = "_"

  // `None` - no declaration, `Some(None)` - declared nodefault, `Some(Some(String))` - declared default
  override val declaredDefaultTagName: Some[Some[String]] = Some(Some(ImpliedDefaultTagName))

  //override val effectiveTagsMap: Map[String, CTag] = ???

}


class CRecordTypeDef(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.RECORD
) with CDatumType {

  override type This = CRecordTypeDef

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _, this)).toList
  }

  // TODO check for dupes
  private val declaredFieldsMap: Map[String, CField] = declaredFields.map { ct => (ct.name, ct) }(collection.breakOut)

  @deprecated
  def effectiveFieldsMap: Map[String, CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveFieldsMap)

  @deprecated
  private lazy val _effectiveFieldsMap = {
    linearizedParents.flatMap(_.effectiveFieldsMap) ++ declaredFieldsMap
  } groupBy { case (fn, _f) =>
    fn
  } map { case (fn, nfseq: Seq[(String, CField)]) =>
    (fn, nfseq.map { case (_fn, f) => f })
  } map { case (fn, fseq: Seq[CField]) =>
    (fn, effectiveField(declaredFieldsMap.get(fn), fseq))
  }

  def effectiveFields: Seq[CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveFields)

  private lazy val _effectiveFields: Seq[CField] = {
    val m: mutable.LinkedHashMap[String, mutable.Builder[CField, Seq[CField]]] = new mutable.LinkedHashMap
    for (f <- linearizedParents.flatMap(_._effectiveFields) ++ declaredFields) {
      m.getOrElseUpdate(f.name, Seq.newBuilder[CField]) += f
    }
    val imb = Seq.newBuilder[CField]
    for ((fn, nfs) <- m) {
      imb += effectiveField(declaredFieldsMap.get(fn), nfs.result())
    }
    imb.result()
  }

  private def effectiveField(declaredFieldOpt: Option[CField], superfields: Seq[CField]): CField = {
    declaredFieldOpt match {
      case Some(df) => // check if declared field is compatible with all (if any) overridden ones
        superfields foreach { st =>
          if (!df.compatibleWith(st)) {
            ctx.errors.add(
              CError(
                csf.filename, csf.position(df.psi),
                s"Type `${
                  df.typeRef.resolved.name.name
                }` of field `${df.name}` is not a subtype of its parent field type or declares incompatible default tag`${
                  st.typeRef.resolved.name.name
                }`"
              )
            )
          }
        }
        df
      case None => // find the most narrow field among inherited ones
        superfields.find(st => superfields.forall(_.typeRef.resolved.isAssignableFrom(st.typeRef.resolved))) match {
          case Some(narrowest) =>
            narrowest // TODO check the narrowest field is default-tag-compatible with the rest
          case None =>
            ctx.errors.add(
              CError(
                csf.filename, csf.position(psi.getQid), s"Multiple inherited `${superfields.head.name}` fields of types ${
                  superfields.map(_.typeRef.resolved.name.name).mkString("`", "`, `", "`")
                } must be overridden with common subtype"
              )
            )
            superfields.head // there must be at least one
        }
    }
  }

}

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl, val host: CRecordTypeDef)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val isAbstract: Boolean = psi.getAbstract ne null

  val valueType: CValueType = new CValueType(csf, psi.getValueTypeRef)

  val typeRef: CTypeRef = valueType.typeRef

  def superfields: Seq[CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _superfields)

  private lazy val _superfields = host.linearizedParents.flatMap(_.effectiveFields.find(_.name == name))

  def compatibleWith(superField: CField): Boolean = superField.typeRef.resolved.isAssignableFrom(typeRef.resolved) && (
      superField.effectiveDefaultTagName match {
        case None => // super has no effective default tag (nodefault)
          true
        case Some(tagName) => // super has effective default tag
          valueType.defaultDeclarationOpt match {
            case None => true // we don't have default tag declaration
            case Some(Some(`tagName`)) => true // we have the same default tag declaration as super
            case _ => false // we have either nodefault or some default different from super's
          }
      }
      )
//  (
//      valueType.defaultDeclarationOpt match {
//        case None => true // no default override
//        case Some(superField.effectiveDefaultTagName) => true // same as super
//        case Some(Some(x)) => superField.effectiveDefaultTagName.isEmpty // different from super but it has no default
//      }
//      )

  // `None` - nodefault, `Some(String)` - effective default tag name
  def effectiveDefaultTagName: Option[String] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveDefaultTagName)

  // explicit no/default on the field > no/default from super field(s) > default on type > default on type super type(s)
  private lazy val _effectiveDefaultTagName: Option[String] = {
    valueType.defaultDeclarationOpt match {
      case Some(explicitDefault: Option[String]) => // field has default declaration (maybe `nodefault`)
        explicitDefault
      case None => // field doesn't have default declaration
        // get effective default declarations from superfields, assert they are the same (ignore Nones), return
        superfields.flatMap(_.effectiveDefaultTagName).distinct match {
          case Seq(theone) => // all superfields (that have effective default tag) have the same one
            Some(theone)
          case Seq() => // no superfields (that have effective default tag)
            valueType.typeRef.resolved.declaredDefaultTagName.flatten // FIXME use type default etc.

          case multiple => // more than one distinct effective default tag on superfields
            ctx.errors.add(
              CError(
                csf.filename, csf.position(psi.getValueTypeRef),
                s"Field `$name` inherits from fields with different default tags ${
                  multiple.mkString("`", "`, `", "`")
                }"
              )
            )
            None // TODO pick one of the tags so child fields don't think we have nodefault?
        }
    }
  }

}


trait CMapType extends CType with CDatumType {self =>

  override type This >: this.type <: CMapType {type This <: self.This}

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueValueType: CValueType

  final val valueTypeRef: CTypeRef = valueValueType.typeRef

  override val kind: CTypeKind = CTypeKind.MAP

  protected def cast(ctype: CType): CMapType = ctype.asInstanceOf[CMapType]

}

class CAnonMapType(override val name: CAnonMapTypeName)(implicit val ctx: CContext) extends {

  override val valueValueType: CValueType = name.valueValueType

} with CMapType {

  override type This = CAnonMapType

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind &&
        keyTypeRef.resolved == subtype.asInstanceOf[CMapType].keyTypeRef.resolved &&
        valueTypeRef.resolved.isAssignableFrom(cast(subtype).valueTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[CAnonMapType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonMapType] = valueTypeRef.resolved.linearizedParents.map { vp =>
    ctx.getAnonMapOf(keyTypeRef.resolved, vp).get // FIXME map[K, Super] might not have been referenced in the schema?
  }

}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends {

  val valueValueType: CValueType = new CValueType(csf, psi.getAnonMap.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.MAP) with CMapType {

  override type This = CMapTypeDef

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRef) // TODO check it's not a vartype?

}


trait CListType extends CType with CDatumType {self =>

  override type This >: this.type <: CListType {type This <: self.This}

  val name: CTypeName

  val elementValueType: CValueType

  final val elementTypeRef: CTypeRef = elementValueType.typeRef

  override val kind: CTypeKind = CTypeKind.LIST

  protected def cast(ctype: CType): CListType = ctype.asInstanceOf[CListType]

}


class CAnonListType(override val name: CAnonListTypeName)(implicit val ctx: CContext) extends {

  override val elementValueType: CValueType = name.elementValueType

} with CListType {

  override type This = CAnonListType

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind && elementTypeRef.resolved.isAssignableFrom(cast(subtype).elementTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[CAnonListType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonListType] = elementTypeRef.resolved.linearizedParents.flatMap(
    ctx.getAnonListOf // FIXME list[Super] might not exist - autocreate
  )

}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

  override val elementValueType: CValueType = new CValueType(csf, psi.getAnonList.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.LIST) with CListType {

  override type This = CListTypeDef

}


class CEnumTypeDef(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.ENUM
) with CDatumType {

  override type This = CEnumTypeDef

  val values: Seq[CEnumValue] = {
    @Nullable val body = psi.getEnumTypeBody
    if (body == null) Nil else body.getEnumMemberDeclList.map(new CEnumValue(csf, _)).toList
  }

}

class CEnumValue(csf: CSchemaFile, psi: SchemaEnumMemberDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

}


class CPrimitiveTypeDef(csf: CSchemaFile, override val psi: SchemaPrimitiveTypeDef)(implicit ctx: CContext)
    extends CTypeDef(
      csf, psi, CTypeKind.forKeyword(psi.getPrimitiveTypeKind.name)
    ) with CDatumType {

  override type This = CPrimitiveTypeDef

}


class CSupplement(csf: CSchemaFile, val psi: SchemaSupplementDef)(implicit val ctx: CContext) {

  val sourceRef: CTypeDefRef = CTypeRef(csf, psi.sourceRef)

  val targetRefs: Seq[CTypeDefRef] = psi.supplementedRefs().map(CTypeRef(csf, _))

}
