/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.intellij.psi.PsiElement
import io.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.annotation.meta.getter
import scala.collection.JavaConversions._
import scala.collection.mutable


abstract class CType(implicit val ctx: CContext) {self =>

  type This >: this.type <: CType {type This <: self.This}

  val name: CTypeName

  val kind: CTypeKind

  def isAssignableFrom(subtype: CType): Boolean

  def linearizedParents: Seq[This]

  /** Immediate parents of this type in order of increasing priority */
  def getLinearizedParentsReversed: java.lang.Iterable[This] = linearizedParents.reverse // TODO phase guard?

  lazy val selfRef: CTypeRef = CTypeRef(this)

}


abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit ctx: CContext) extends CType {self =>

  override type This >: this.type <: CTypeDef {type This <: self.This}

  @scala.beans.BeanProperty
  val name: CTypeFqn = new CTypeFqn(csf, csf.namespace.fqn, psi)

  val isAbstract: Boolean = psi.getAbstract != null

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

  final override type This = CVarTypeDef

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredTagsMap: Map[String, CTag] = declaredTags.map { ct => (ct.name, ct) }(collection.breakOut)

  def effectiveTags: Seq[CTag] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveTags)

  private lazy val _effectiveTags: Seq[CTag] = {
    val m: mutable.LinkedHashMap[String, mutable.Builder[CTag, Seq[CTag]]] = new mutable.LinkedHashMap
    for (t <- linearizedParents.flatMap(_.effectiveTags) ++ declaredTags) {
      m.getOrElseUpdate(t.name, Seq.newBuilder[CTag]) += t
    }
    val imb = Seq.newBuilder[CTag]
    for ((fn, nfs) <- m) {
      imb += effectiveTag(declaredTagsMap.get(fn), nfs.result())
    }
    imb.result()
  }

  // TODO tags with override modifier should check they have superfield(s)

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

  def dataType(polymorphic: Boolean, defaultTagName: Option[String]): CDataType = new CDataType(
    csf, // TODO this schema file might not be the one we expect (i.e. not the one where the data type is (maybe indirectly) referenced)
    polymorphic,
    selfRef,
    if (effectiveTags.exists { et => defaultTagName.contains(et.name) }) defaultTagName else None
  )

}

class CTag(val csf: CSchemaFile, val name: String, val typeRef: CTypeRef, @Nullable val psi: PsiElement) {

  def this(csf: CSchemaFile, psi: SchemaVarTagDecl)(implicit ctx: CContext) =
    this(csf, psi.getQid.getCanonicalName, CTypeRef(csf, psi.getTypeRef), psi)

  def compatibleWith(st: CTag): Boolean = st.typeRef.resolved.isAssignableFrom(typeRef.resolved)

}

trait CDatumType extends CType {self =>

  override type This >: this.type <: CDatumType {type This <: self.This}

  protected val csf: CSchemaFile

  @(Nullable@getter)
  @Nullable protected val psi: PsiElement

  val impliedTag: CTag = new CTag(csf, CDatumType.ImpliedDefaultTagName, selfRef, psi)

  def dataType(polymorphic: Boolean): CDataType = new CDataType(csf, polymorphic, selfRef, None)

}

object CDatumType {

  val ImpliedDefaultTagName: String = "_" // must not be valid tag name (or refactor JavaGen.dtrn(...))

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

  @deprecated("use effectiveFields")
  def effectiveFieldsMap: Map[String, CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveFieldsMap)

  @deprecated("use effectiveFields")
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
                }` of field `${df.name}` is not a subtype of its parent field type or declares different default tag`${
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

  // TODO fields with override modifier should check they have superfield(s)

  val name: String = psi.getQid.getCanonicalName

  val isAbstract: Boolean = psi.getAbstract ne null

  val valueDataType: CDataType = new CDataType(csf, psi.getValueTypeRef)

  val typeRef: CTypeRef = valueDataType.typeRef

  def superfields: Seq[CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _superfields)

  private lazy val _superfields = host.linearizedParents.flatMap(_.effectiveFields.find(_.name == name))

  def compatibleWith(superField: CField): Boolean = superField.typeRef.resolved.isAssignableFrom(typeRef.resolved) && (
      superField.effectiveDefaultTagName match {
        case None => // super has no effective default tag (nodefault)
          true
        case Some(tagName) => // super has effective default tag
          valueDataType.effectiveDefaultTagName match {
            case Some(`tagName`) => true // we have the same default tag declaration as super
            case None => false // we don't have default tag declaration
            case _ => false // we have either no default or some default different from super's
          }
      }
      )

  // `None` - no default, `Some(String)` - effective default tag name
  def effectiveDefaultTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, valueDataType.effectiveDefaultTagName
  )

//  // explicit no/default on the field > no/default from super field(s) > effective default on field type
//  private lazy val _effectiveDefaultTagName: Option[String] = {
//    valueDataType.defaultDeclarationOpt match {
//      case Some(explicitDefault: Option[String]) => // field has default declaration (maybe `nodefault`)
//        explicitDefault
//      case None => // field doesn't have default declaration
//        // get effective default declarations from superfields, assert they are the same (ignore Nones), return
//        superfields.flatMap(_.effectiveDefaultTagName).distinct match {
//          case Seq(theone) => // all superfields (that have effective default tag) have the same one
//            Some(theone)
//          case Seq() => // no superfields (that have effective default tag)
//            valueDataType.typeRef.resolved.effectiveDefaultTagName
//          case multiple => // more than one distinct effective default tag on superfields
//            ctx.errors.add(
//              CError(
//                csf.filename, csf.position(psi.getValueTypeRef),
//                s"Field `$name` inherits from fields with different default tags ${
//                  multiple.mkString("`", "`, `", "`")
//                }"
//              )
//            )
//            None // TODO pick one of the tags so child fields don't think we have nodefault?
//        }
//    }
//  }

}


trait CMapType extends CType with CDatumType {self =>

  override type This >: this.type <: CMapType {type This <: self.This}

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueDataType: CDataType

  final val valueTypeRef: CTypeRef = valueDataType.typeRef

  def effectiveDefaultValueTagName: Option[String] = ??? // FIXME implement similar to list

  override val kind: CTypeKind = CTypeKind.MAP

  protected def cast(ctype: CType): CMapType = ctype.asInstanceOf[CMapType]

}

class CAnonMapType(override val name: CAnonMapTypeName)(implicit ctx: CContext) extends {

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override val valueDataType: CDataType = name.valueDataType

} with CMapType {

  def this(keyTypeRef: CTypeRef, elementDataType: CDataType)(implicit ctx: CContext) =
    this(new CAnonMapTypeName(keyTypeRef, elementDataType))

  override type This = CAnonMapType

  override protected val csf: CSchemaFile = valueDataType.csf

  @Nullable override protected val psi: PsiElement = null

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind &&
        keyTypeRef.resolved == subtype.asInstanceOf[CMapType].keyTypeRef.resolved &&
        valueTypeRef.resolved.isAssignableFrom(cast(subtype).valueTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[CAnonMapType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonMapType] = valueDataType.typeRef.resolved match {
    case vt: CVarTypeDef => valueDataType.effectiveDefaultTagName.map { tagName =>
      if (!vt.effectiveTags.exists(_.name == tagName)) ctx.errors.add(
        CError(csf.filename, CErrorPosition.NA, s"Tag `$tagName` is not defined for union type `${vt.name.name}`")
      )
      ctx.getOrCreateAnonMapOf(keyTypeRef, vt.dataType(valueDataType.polymorphic, None))
    }.toSeq ++ vt.linearizedParents.map { vst =>
      ctx.getOrCreateAnonMapOf(keyTypeRef,
        vst.dataType(valueDataType./*TODO or false?*/ polymorphic, valueDataType.effectiveDefaultTagName)
      )
    }
    case et: CDatumType => et.linearizedParents.map { vst =>
      ctx.getOrCreateAnonMapOf(keyTypeRef, vst.dataType(valueDataType.polymorphic/*TODO or false?*/))
    }
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends {

  val valueDataType: CDataType = new CDataType(csf, psi.getAnonMap.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.MAP) with CMapType {

  override type This = CMapTypeDef

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRef) // TODO check it's not a vartype?

}


trait CListType extends CType with CDatumType {self =>

  override type This >: this.type <: CListType {type This <: self.This}

  val name: CTypeName

  val elementDataType: CDataType

  final val elementTypeRef: CTypeRef = elementDataType.typeRef

  def effectiveDefaultElementTagName: Option[String]

  override val kind: CTypeKind = CTypeKind.LIST

  protected def cast(ctype: CType): CListType = ctype.asInstanceOf[CListType]

}


class CAnonListType(override val name: CAnonListTypeName)(implicit ctx: CContext) extends {

  override val elementDataType: CDataType = name.elementDataType

} with CListType {

  def this(elementDataType: CDataType)(implicit ctx: CContext) = this(new CAnonListTypeName(elementDataType))

  override type This = CAnonListType

  override protected val csf: CSchemaFile = elementDataType.csf

  @Nullable override protected val psi: PsiElement = null

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind && elementTypeRef.resolved.isAssignableFrom(cast(subtype).elementTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  override def linearizedParents: Seq[CAnonListType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonListType] = elementDataType.typeRef.resolved match {
    case et: CVarTypeDef => elementDataType.effectiveDefaultTagName.map { tagName =>
      if (!et.effectiveTags.exists(_.name == tagName)) ctx.errors.add(
        CError(csf.filename, CErrorPosition.NA, s"Tag `$tagName` is not defined for union type `${et.name.name}`")
      )
      ctx.getOrCreateAnonListOf(et.dataType(elementDataType.polymorphic, None))
    }.toSeq ++ et.linearizedParents.map { est =>
      ctx.getOrCreateAnonListOf(
        est.dataType(elementDataType./*TODO or false?*/ polymorphic, elementDataType.effectiveDefaultTagName)
      )
    }
    case et: CDatumType => et.linearizedParents.map { est =>
      ctx.getOrCreateAnonListOf(est.dataType(elementDataType.polymorphic/*TODO or false?*/))
    }
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  final override def effectiveDefaultElementTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, elementDataType.effectiveDefaultTagName
  )

}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

  override val elementDataType: CDataType = new CDataType(csf, psi.getAnonList.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.LIST) with CListType {

  override type This = CListTypeDef

  // `None` - no default, `Some(String)` - effective default tag name
  def effectiveDefaultElementTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, _effectiveDefaultElementTagName
  )

  private lazy val _effectiveDefaultElementTagName: Option[String] = {
    linearizedParents.foreach { st =>
      if (!elementDataType.compatibleWith(st.elementDataType)) ctx.errors.add(
        CError(
          csf.filename,
          csf.position(psi.getAnonList),
          s"Type `$name` inherits from list type `${st.name.name}` with different default element tag `${st.elementDataType.effectiveDefaultTagName.get}`"
        )
      )
    }
    elementDataType.effectiveDefaultTagName
  }

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
