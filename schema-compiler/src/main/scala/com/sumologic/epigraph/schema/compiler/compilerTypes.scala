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

  /** Common type of this type's supertypes. */
  type Super >: self.type <: CType {type Super <: self.Super}

  val name: CTypeName

  val kind: CTypeKind

  def isAssignableFrom(subtype: CType): Boolean

  def linearizedParents: Seq[Super]

  //def supertypes: Seq[Super]

  /** Immediate parents of this type in order of increasing priority */
  def getLinearizedParentsReversed: java.lang.Iterable[Super] = linearizedParents.reverse // TODO phase guard?

  lazy val selfRef: CTypeRef = CTypeRef(this)

}


abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit ctx: CContext) extends CType {self =>

  override type Super >: this.type <: CTypeDef {type Super <: self.Super}

  val name: CTypeFqn = new CTypeFqn(csf, csf.namespace.fqn, psi)

  val isAbstract: Boolean = psi.getAbstract != null

  /** References to types this type explicitly extends. */
  val extendedTypeRefs: Seq[CTypeDefRef] = {
    @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
    if (sed == null) Nil else sed.getFqnTypeRefList.map(CTypeRef(csf, _))
  }

  /** Types this type explicitly extends. */
  private def extendedTypes: Seq[CTypeDef] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _extendedTypes)

  private lazy val _extendedTypes: Seq[CTypeDef] = extendedTypeRefs.map(_.resolved)

  /** References to types this type supplements (injects itself into). */
  val supplementedTypeRefs: Seq[CTypeDefRef] = {
    @Nullable val ssd: SchemaSupplementsDecl = psi.getSupplementsDecl
    if (ssd == null) Nil else ssd.getFqnTypeRefList.map(CTypeRef(csf, _))
  }

  /** Accumulator for types injected (via `supplements` clause or `supplement` declaration) into this type. */
  val injectedTypes: ConcurrentLinkedQueue[CTypeDef] = new ConcurrentLinkedQueue

  def extendedAndInjectedTypes: Seq[CTypeDef] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _extendedAndInjectedTypes)

  private lazy val _extendedAndInjectedTypes: Seq[CTypeDef] = extendedTypes ++ injectedTypes

  /*override*/ def supertypes: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _computedSupertypes.getOrElse(Nil))

  private var _computedSupertypes: Option[Seq[Super]] = None

  def computeSupertypes(visited: mutable.Stack[CTypeDef]): Unit = {
    if (_computedSupertypes.isEmpty) {
      val thisIdx = visited.indexOf(this)
      visited.push(this)
      if (thisIdx == -1) {
        extendedAndInjectedTypes foreach (_.computeSupertypes(visited))
        val (good, bad) = extendedAndInjectedTypes.partition(_.kind == kind)
        bad foreach { st =>
          val stSource = if (injectedTypes.contains(st)) "injected" else "declared"
          ctx.errors.add(
            CError(
              csf.filename, csf.position(psi.getQid), // TODO use injection source/position for injection failures
              s"Type ${name.name} (of '${kind.keyword}' kind) is not compatible with $stSource supertype ${st.name.name} (of `${st.kind.keyword}` kind)"
            )
          )
        }
        // now that we (kind of) checked all good supertypes to be of the same kind and hence instances of `Super`
        _computedSupertypes = Some(good.asInstanceOf[Seq[Super]].flatMap { st => st.supertypes :+ st }.distinct)
      } else {
        ctx.errors.add(
          CError(
            csf.filename, csf.position(psi.getQid),
            s"Cyclic inheritance: type ${visited.view(0, thisIdx + 2).reverseIterator.map(_.name.name).mkString(" < ")}"
          )
        )
        _computedSupertypes = Some(Nil)
      }
      visited.pop()
    }
  }

  /** Declared parents of this type in order of increasing priority. */
  def parents: Seq[Super] = ctx.after( // after compute supertypes phase all these should be instances of `Super`
    CPhase.COMPUTE_SUPERTYPES, null, extendedAndInjectedTypes.asInstanceOf[Seq[Super]]
  )

  /** Immediate parents of this type in order of decreasing priority. */
  def linearizedParents: Seq[Super] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[Super] = parents.foldLeft[Seq[Super]](Nil) { (acc, p) =>
    if (acc.contains(p) || parents.exists(_.supertypes.contains(p))) acc else p +: acc
  }

  /** Linearized supertypes of this type in order of decreasing priority. */
  def linearizedSupertypes: Seq[Super] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedSupertypes)

  private lazy val _linearizedSupertypes: Seq[Super] = parents.foldLeft[Seq[Super]](Nil) { (acc, p) =>
    p.linearization.filterNot(acc.contains) ++ acc
  }

  /** Linearization of this type (i.e. this type and all of its supertypes in order of decreasing priority). */
  def linearization: Seq[Super] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearized)

  private lazy val _linearized: Seq[Super] = this.asInstanceOf/*scalac bug*/ [self.type] +: linearizedSupertypes

  override def isAssignableFrom(subtype: CType): Boolean = subtype match {
    case subDef: CTypeDef if kind == subtype.kind && subDef.linearization.contains(this) => true
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

  final override type Super = CVarTypeDef

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

  override type Super >: self.type <: CDatumType {type Super <: self.Super}

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

  override type Super = CRecordTypeDef

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

  override type Super >: self.type <: CMapType {type Super <: self.Super}

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueDataType: CDataType

  final val valueTypeRef: CTypeRef = valueDataType.typeRef

  def effectiveDefaultValueTagName: Option[String]// = ??? // FIXME implement similar to list

  override val kind: CTypeKind = CTypeKind.MAP

  protected def cast(ctype: CType): CMapType = ctype.asInstanceOf[CMapType]

}

class CAnonMapType(override val name: CAnonMapTypeName)(implicit ctx: CContext) extends {

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override val valueDataType: CDataType = name.valueDataType

} with CMapType {

  def this(keyTypeRef: CTypeRef, elementDataType: CDataType)(implicit ctx: CContext) =
    this(new CAnonMapTypeName(keyTypeRef, elementDataType))

  override type Super = CAnonMapType

  override protected val csf: CSchemaFile = valueDataType.csf

  @Nullable override protected val psi: PsiElement = null

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind &&
        keyTypeRef.resolved == subtype.asInstanceOf[CMapType].keyTypeRef.resolved &&
        valueTypeRef.resolved.isAssignableFrom(cast(subtype).valueTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[CAnonMapType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonMapType] = {
    val parents = valueDataType.typeRef.resolved match {
      case vt: CVarTypeDef => valueDataType.effectiveDefaultTagName.map { tagName =>
        if (!vt.effectiveTags.exists(_.name == tagName)) ctx.errors.add(
          CError(csf.filename, CErrorPosition.NA, s"Tag `$tagName` is not defined for union type `${vt.name.name}`")
        )
        ctx.getOrCreateAnonMapOf(keyTypeRef, vt.dataType(valueDataType.polymorphic, None))
      }.toSeq ++ vt.linearizedParents.map { vst =>
        ctx.getOrCreateAnonMapOf(
          keyTypeRef,
          vst.dataType(valueDataType./*TODO or false?*/ polymorphic, valueDataType.effectiveDefaultTagName)
        )
      }
      case et: CDatumType => et.linearizedParents.map { vst =>
        ctx.getOrCreateAnonMapOf(keyTypeRef, vst.dataType(valueDataType.polymorphic/*TODO or false?*/))
      }
      case unknown => throw new UnsupportedOperationException(unknown.toString)
    }
    parents foreach (_.linearizedParents) // trigger parents linearization
    parents
  }

  final override def effectiveDefaultValueTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, valueDataType.effectiveDefaultTagName
  )

}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends {

  val valueDataType: CDataType = new CDataType(csf, psi.getAnonMap.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.MAP) with CMapType {

  override type Super = CMapTypeDef

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRef) // TODO check it's not a vartype?

  // `None` - no default, `Some(String)` - effective default tag name
  override def effectiveDefaultValueTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, _effectiveDefaultElementTagName
  )

  private lazy val _effectiveDefaultElementTagName: Option[String] = {
    linearizedParents.foreach { st =>
      if (!valueDataType.compatibleWith(st.valueDataType)) ctx.errors.add(
        CError(
          csf.filename,
          csf.position(psi.getAnonMap),
          s"Type `$name` inherits from map type `${st.name.name}` with different default value tag `${st.valueDataType.effectiveDefaultTagName.get}`"
        )
      )
    }
    valueDataType.effectiveDefaultTagName
  }

  // FIXME include anon map in supertypes

}


trait CListType extends CType with CDatumType {self =>

  override type Super >: self.type <: CListType {type Super <: self.Super}

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

  override type Super = CAnonListType

  override protected val csf: CSchemaFile = elementDataType.csf

  @Nullable override protected val psi: PsiElement = null

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind && elementTypeRef.resolved.isAssignableFrom(cast(subtype).elementTypeRef.resolved)

  /** Immediate parents of this type in order of decreasing priority */
  override def linearizedParents: Seq[CAnonListType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonListType] = {
    val parents = elementDataType.typeRef.resolved match {
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
    parents foreach (_.linearizedParents) // trigger parents linearization
    parents
  }

  final override def effectiveDefaultElementTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, elementDataType.effectiveDefaultTagName
  )

}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

  override val elementDataType: CDataType = new CDataType(csf, psi.getAnonList.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.LIST) with CListType {

  override type Super = CListTypeDef

  // `None` - no default, `Some(String)` - effective default tag name
  override def effectiveDefaultElementTagName: Option[String] = ctx.after(
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

  // FIXME include anon list in supertypes

}


class CEnumTypeDef(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.ENUM
) with CDatumType {

  override type Super = CEnumTypeDef

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

  override type Super = CPrimitiveTypeDef

}


class CSupplement(csf: CSchemaFile, val psi: SchemaSupplementDef)(implicit val ctx: CContext) {

  val sourceRef: CTypeDefRef = CTypeRef(csf, psi.sourceRef)

  val targetRefs: Seq[CTypeDefRef] = psi.supplementedRefs().map(CTypeRef(csf, _))

}
