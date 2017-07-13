/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 6/10/16. */

package ws.epigraph.compiler

import java.util
import java.util.concurrent.ConcurrentLinkedQueue

import com.intellij.psi.PsiElement
import org.jetbrains.annotations.Nullable
import ws.epigraph.annotations.Annotations
import ws.epigraph.psi.DefaultPsiProcessingContext
import ws.epigraph.refs.ImportAwareTypesResolver
import ws.epigraph.schema.parser.SchemaPsiParserUtil
import ws.epigraph.schema.parser.psi._
import ws.epigraph.types.DatumTypeApi

import scala.annotation.meta.getter
import scala.collection.JavaConversions._
import scala.collection.{JavaConverters, mutable}
import scala.util.matching.Regex


abstract class CType(implicit val ctx: CContext) {self =>

  /** Common type of this type's supertypes. */
  type Super >: self.type <: CType {type Super <: self.Super}

  val name: CTypeName

  val kind: CTypeKind

  lazy val selfRef: CTypeRef = CTypeRef(this)

  protected val csf: CSchemaFile

  /** All (valid) supertypes of this type. After [[CPhase.RESOLVE_TYPEREFS]]. */
  def supertypes: Seq[Super]

  /** Sanitized declared parents of this type in order of increasing priority. After [[CPhase.RESOLVE_TYPEREFS]]. */
  def parents: Seq[Super]

  /** Immediate parents of this type in order of decreasing priority. After [[CPhase.COMPUTE_SUPERTYPES]]. */
  def linearizedParents: Seq[Super]

  /** Immediate parents of this type in order of increasing priority. After [[CPhase.COMPUTE_SUPERTYPES]]. */
  final def getLinearizedParentsReversed: java.lang.Iterable[Super] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, linearizedParents.reverse
  )

  /** Linearized supertypes of this type in order of decreasing priority. After [[CPhase.RESOLVE_TYPEREFS]]. */
  final def linearizedSupertypes: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _linearizedSupertypes)

  private lazy val _linearizedSupertypes: Seq[Super] = parents.foldLeft[Seq[Super]](Nil) { (acc, p) =>
    p.linearization.filterNot(acc.contains) ++ acc
  }

  /**
   * Linearization of this type (i.e. this type and all of its supertypes in order of decreasing priority).
   * After [[CPhase.RESOLVE_TYPEREFS]].
   */
  final def linearization: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _linearization)

  private lazy val _linearization: Seq[Super] = this.asInstanceOf/*scalac bug*/ [self.type] +: linearizedSupertypes

  /** After [[CPhase.COMPUTE_SUPERTYPES]]. */
  final def isAssignableFrom(subtype: CType): Boolean = subtype.kind == kind && subtype.linearization.contains(this)

  def dataType: CDataType = new CDataType(csf, selfRef, None)

}


abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit ctx: CContext) extends CType {self =>

  //override type Super >: self.type <: CType {type Super <: self.Super} // idea scala bug

  val name: CTypeFqn = new CTypeFqn(csf, csf.namespace.fqn, psi)

  val isAbstract: Boolean = psi.getAbstract != null

  /** References to types this type supplements (injects itself into). */
  val supplementedTypeRefs: Seq[CTypeDefRef] = {
    @Nullable val ssd: SchemaSupplementsDecl = psi.getSupplementsDecl
    if (ssd == null) Nil else ssd.getQnTypeRefList.map(CTypeRef(csf, _))
  }

  /** References to types this type explicitly extends. */
  val extendedTypeRefs: Seq[CTypeDefRef] = {
    @Nullable val sed: SchemaExtendsDecl = psi.getExtendsDecl
    if (sed == null) Nil else sed.getQnTypeRefList.map(CTypeRef(csf, _))
  }

  /** Types this type explicitly extends. */
  private def extendedTypes: Seq[CTypeDef] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _extendedTypes)

  private lazy val _extendedTypes: Seq[CTypeDef] = extendedTypeRefs.map(_.resolved)

  /** Accumulator for types injected (via `supplements` clause or `supplement` declaration) into this type. */
  val injectedTypes: ConcurrentLinkedQueue[CTypeDef] = new ConcurrentLinkedQueue

  def extendedAndInjectedTypes: Seq[CTypeDef] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _extendedAndInjectedTypes)

  private lazy val _extendedAndInjectedTypes: Seq[CTypeDef] = extendedTypes ++ injectedTypes

  /*override*/ def supertypes: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _computedSupertypes.getOrElse(Nil))

  private var _computedSupertypes: Option[Seq[Super]] = None

  def computeSupertypes(visited: mutable.Stack[CType]): Unit = {
    if (_computedSupertypes.isEmpty) {
      val thisIdx = visited.indexOf(this)
      visited.push(this)
      if (thisIdx == -1) {
        extendedAndInjectedTypes foreach (_.computeSupertypes(visited))
        val (good, bad) = parents.partition(_.kind == kind)
        bad foreach { st =>
          val stSource = if (injectedTypes.contains(st)) "injected" else "declared"
          ctx.errors.add(
            CMessage.error(
              csf.filename, csf.position(psi.getQid), // TODO use injection source/position for injection failures
              s"Type ${name.name} (of '${kind.keyword}' kind) is not compatible with $stSource supertype ${st.name.name} (of `${st.kind.keyword}` kind)"
            )
          )
        }
        // now that we (kind of) checked all good supertypes to be of the same kind and hence instances of `Super`
        _computedSupertypes = Some(good.asInstanceOf[Seq[Super]].flatMap { st => st.supertypes :+ st }.distinct)
      } else {
        ctx.errors.add(
          CMessage.error(
            csf.filename, csf.position(psi.getQid),
            s"Cyclic inheritance: type ${visited.view(0, thisIdx + 2).reverseIterator.map(_.name.name).mkString(" < ")}"
          )
        )
        _computedSupertypes = Some(Nil)
      }
      visited.pop()
    }
  }

  /** Sanitized declared parents of this type in order of increasing priority. After [[CPhase.RESOLVE_TYPEREFS]]. */
  def parents: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _parents)

  private lazy val _parents: Seq[Super] = extendedAndInjectedTypes.filter(_.kind == kind).asInstanceOf[Seq[Super]]

  /** Immediate parents of this type in order of decreasing priority. */
  def linearizedParents: Seq[Super] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[Super] = parents.foldLeft[Seq[Super]](Nil) { (acc, p) =>
    if (acc.contains(p) || parents.exists(_.supertypes.contains(p))) acc else p +: acc
  }

//  override def isAssignableFrom(subtype: CType): Boolean = subtype match {
//    case subDef: CTypeDef if kind == subtype.kind && subDef.linearization.contains(this) => true
//    case _ => false
//  }

  def annotations: Annotations = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _computedAnnotations)

  private lazy val _computedAnnotations: Annotations = CTypeUtil.buildAnnotations(csf, annotationsPsiOpt)

  protected def annotationsPsiOpt: Option[java.util.List[SchemaAnnotation]] = None

}

object CTypeDef {

  def apply(csf: CSchemaFile, stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CTypeDef = stdw.getElement match {
    case typeDef: SchemaEntityTypeDef => new CEntityTypeDef(csf, typeDef)
    case typeDef: SchemaRecordTypeDef => new CRecordTypeDef(csf, typeDef)
    case typeDef: SchemaMapTypeDef => new CMapTypeDef(csf, typeDef)
    case typeDef: SchemaListTypeDef => new CListTypeDef(csf, typeDef)
    case typeDef: SchemaEnumTypeDef => new CEnumTypeDef(csf, typeDef)
    case typeDef: SchemaPrimitiveTypeDef => new CPrimitiveTypeDef(csf, typeDef)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def declaredRetroTagName(@Nullable srd: SchemaRetroDecl): Option[Option[String]] = {
    if (srd == null) {
      None
    } else {
      @Nullable val vtr = srd.getEntityTagRef
      if (vtr == null) {
        Some(None)
      } else {
        Some(Some(vtr.getQid.getCanonicalName))
      }
    }
  }

}


class CEntityTypeDef(csf: CSchemaFile, override val psi: SchemaEntityTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.ENTITY
) {

  final override type Super = CEntityTypeDef

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getEntityTypeBody
    if (body == null) Nil else body.getEntityTagDeclList.map(new CTag(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredTagsMap: Map[String, CTag] = declaredTags.map { ct => (ct.name, ct) }(collection.breakOut)

  def effectiveTags: Seq[CTag] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _effectiveTags)

  def findDeclaredTag(name: String): Option[CTag] = declaredTags.find(_.name == name)

  def findEffectiveTag(name: String): Option[CTag] = effectiveTags.find(_.name == name)

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
              CMessage.error(
                csf.filename, csf.position(dt.locationPsi),
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
              CMessage.error(
                csf.filename, csf.position(psi.getQid), s"Multiple inherited tags `${supertags.head.name}` of types ${
                  supertags.map(_.typeRef.resolved.name.name).mkString("`", "`, `", "`")
                } must be overridden with common subtype"
              )
            )
            supertags.head // there must be at least one
        }
    }
  }

  def dataType(defaultTagName: Option[String]): CDataType = new CDataType(
    csf, // TODO this schema file might not be the one we expect (i.e. not the one where the data type is (maybe indirectly) referenced)
    selfRef,
    if (effectiveTags.exists { et => defaultTagName.contains(et.name) }) defaultTagName else None
  )

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getEntityTypeBody).map(_.getAnnotationList)

}

class CTag private[compiler](
  val csf: CSchemaFile,
  val name: String,
  val typeRef: CTypeRef,
  val declPsiOpt: Option[SchemaEntityTagDecl],
  val locationPsi: PsiElement
)(implicit val ctx: CContext) {

  def this(csf: CSchemaFile, psi: SchemaEntityTagDecl)(implicit ctx: CContext) = this(
    csf,
    NamePatterns.validateTagName(psi.getQid.getCanonicalName, csf, psi),
    CTypeRef(csf, psi.getTypeRef),
    Some(psi),
    psi
  )

  def compatibleWith(st: CTag): Boolean = st.typeRef.resolved.isAssignableFrom(typeRef.resolved)

  def annotations: Annotations = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _computedAnnotations)

  private lazy val _computedAnnotations: Annotations = CTypeUtil.buildAnnotations(csf, declPsiOpt.map(_.getAnnotationList))

  def canEqual(other: Any): Boolean = other.isInstanceOf[CTag]

  override def equals(other: Any): Boolean = other match {
    // should we take annotations into account here too? they may be unavailable at this point
    case that: CTag => (that canEqual this) && name == that.name && typeRef == that.typeRef
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name, typeRef)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

trait CDatumType extends CType {self =>

  override type Super >: self.type <: CDatumType {type Super <: self.Super}

  protected val csf: CSchemaFile

  @(Nullable@getter)
  @Nullable protected val psi: PsiElement

  val impliedTag: CTag = new CTag(csf, CDatumType.ImpliedDefaultTagName, selfRef, None, psi)

  override def dataType: CDataType = new CDataType(csf, selfRef, None)

  def metaDeclPsi: Option[SchemaMetaDecl]

  val declaredMeta: Option[CTypeRef] = metaDeclPsi.map { mdp => CTypeRef(csf, mdp.getQnTypeRef) }

  /** Effective meta-type of this type. After [[CPhase.RESOLVE_TYPEREFS]]. */
  def meta: Option[CDatumType] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, {
    val minSuperMeta: Option[CDatumType] = parents.foldLeft[Option[CDatumType]](None){ case (msm, parent) =>
      (msm, parent.meta) match {
        case (Some(m), Some(p)) =>
          if (m.isAssignableFrom(p)) parent.meta
          else if (p.isAssignableFrom(m)) msm
          else {
            ctx.errors.add(
              CMessage.error(
                csf.filename,
                csf.position(psi),
                "Parent meta-types are not compatible" // TODO better explanation
              )
            )
            None
          }
        case (None, Some(_)) => parent.meta
        case (Some(_), None) => msm
        case (None, None) => None
      }
    }

    val _declaredMeta: Option[CDatumType] = declaredMeta.flatMap{ ref =>
      ref.resolved match {
        case t: CDatumType => Some(t)
        case x =>
          ctx.errors.add(
            CMessage.error(
              csf.filename,
              csf.position(metaDeclPsi.get),
              s"Declared meta-type '${x.name.toString}' is not a datum type"
            )
          )
          None
      }
    }

    (_declaredMeta, minSuperMeta) match {
      case (Some(m), Some(p)) =>
        if (m.isAssignableFrom(p)) minSuperMeta
        else if (p.isAssignableFrom(m)) _declaredMeta
        else {
          ctx.errors.add(
            CMessage.error(
              csf.filename,
              csf.position(metaDeclPsi.get),
              "Parent meta-types are not compatible with current meta-type" // TODO better explanation
            )
          )
          None
        }
      case (None, Some(_)) => minSuperMeta
      case (Some(_), None) => _declaredMeta
      case (None, None) => None
    }
  })

}

object CDatumType {

  val ImpliedDefaultTagName: String = DatumTypeApi.MONO_TAG_NAME // must not be valid tag name (or refactor JavaGen.dtrn(...))

}


class CRecordTypeDef(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.RECORD
) with CDatumType {

  override type Super = CRecordTypeDef

  override def metaDeclPsi: Option[SchemaMetaDecl] = Option(psi.getMetaDecl)
  
  def findDeclaredField(name: String): Option[CField] = declaredFields.find(_.name == name)

  def findEffectiveField(name: String): Option[CField] = effectiveFields.find(_.name == name)

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _, this)).toList
  }

  // TODO check for dupes
  private val declaredFieldsMap: Map[String, CField] = declaredFields.map { f => (f.name, f) }(collection.breakOut)

  def effectiveFields: Seq[CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveFields)

  private lazy val _effectiveFields: Seq[CField] = {
    val m: mutable.LinkedHashMap[String, mutable.Builder[CField, Seq[CField]]] = new mutable.LinkedHashMap
    for (f <- linearizedParents.flatMap(_._effectiveFields) ++ declaredFields) {
      m.getOrElseUpdate(f.name, Seq.newBuilder[CField]) += f
    }
    val imb = Seq.newBuilder[CField]
    for ((fn, nfs) <- m) imb += effectiveField(declaredFieldsMap.get(fn), nfs.result())
    imb.result()
  }

  private def effectiveField(declaredFieldOpt: Option[CField], superfields: Seq[CField]): CField = {
    declaredFieldOpt match {
      case Some(df) => // check if declared field is compatible with all (if any) overridden ones
        superfields foreach { sf =>
          if (!df.compatibleWith(sf)) {
            ctx.errors.add(
              CMessage.error(
                csf.filename, csf.position(df.psi),
                s"Type `${
                  df.typeRef.resolved.name.name
                }` of field `${df.name}` is not a subtype of its parent field type `${
                  sf.typeRef.resolved.name.name
                }` or declares different default tag"
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
              CMessage.error(
                csf.filename, csf.position(psi.getQid), s"Multiple inherited `${superfields.head.name}` fields of types ${
                  superfields.map(_.typeRef.resolved.name.name).mkString("`", "`, `", "`")
                } must be overridden with common subtype"
              )
            )
            superfields.head // there must be at least one
        }
    }
  }

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getRecordTypeBody).map(_.getAnnotationList)
}

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl, val host: CRecordTypeDef)(implicit val ctx: CContext) {

  // TODO fields with override modifier should check they have superfield(s)

  val name: String = NamePatterns.validateFieldName(psi.getQid.getCanonicalName, csf, psi)

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

  def annotations: Annotations = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _computedAnnotations)

  private lazy val _computedAnnotations: Annotations = CTypeUtil.buildAnnotations(csf, Option(psi.getAnnotationList))
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

//  override def isAssignableFrom(subtype: CType): Boolean =
//    subtype.kind == kind &&
//        keyTypeRef.resolved == subtype.asInstanceOf[CMapType].keyTypeRef.resolved &&
//        valueTypeRef.resolved.isAssignableFrom(cast(subtype).valueTypeRef.resolved)

  override def supertypes: Seq[CAnonMapType] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _supertypes)

  private lazy val _supertypes = parents.flatMap { st => st.supertypes :+ st }.distinct

  def parents: Seq[CAnonMapType] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _parents)

  private lazy val _parents: Seq[CAnonMapType] = valueDataType.typeRef.resolved match {

    case vt: CEntityTypeDef => valueDataType.effectiveDefaultTagName match {
      case Some(tagName) => Seq(ctx.getOrCreateAnonMapOf(keyTypeRef, vt.dataType(None))) ++ vt.parents.map(
        etp => ctx.getOrCreateAnonMapOf(
          keyTypeRef, etp.dataType(etp.effectiveTags.find(_.name == tagName).map(_.name))
        )
      )
      case None => vt.parents.map(etp => ctx.getOrCreateAnonMapOf(keyTypeRef, etp.dataType(None)))
    }

    case vt: CDatumType => vt.parents.map(est => ctx.getOrCreateAnonMapOf(keyTypeRef, est.dataType))

    case unknown => throw new UnsupportedOperationException(unknown.toString)

  }

  /** Immediate parents of this type in order of decreasing priority */
  def linearizedParents: Seq[CAnonMapType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonMapType] = {
    val parents = valueDataType.typeRef.resolved match {
      case vt: CEntityTypeDef => valueDataType.effectiveDefaultTagName.map { tagName =>
        if (!vt.effectiveTags.exists(_.name == tagName)) ctx.errors.add(
          CMessage.error(csf.filename, CMessagePosition.NA, s"Tag `$tagName` is not defined for union type `${vt.name.name}`")
        )
        ctx.getOrCreateAnonMapOf(keyTypeRef, vt.dataType(None))
      }.toSeq ++ vt.linearizedParents.map { vst =>
        ctx.getOrCreateAnonMapOf(keyTypeRef, vst.dataType(valueDataType.effectiveDefaultTagName))
      }
      case et: CDatumType => et.linearizedParents.map { vst => ctx.getOrCreateAnonMapOf(keyTypeRef, vst.dataType) }
      case unknown => throw new UnsupportedOperationException(unknown.toString)
    }
    parents foreach (_.linearizedParents) // trigger parents linearization
    parents
  }

  final override def effectiveDefaultValueTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, valueDataType.effectiveDefaultTagName
  )

  override def metaDeclPsi: Option[SchemaMetaDecl] = None
}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends {

  val valueDataType: CDataType = new CDataType(csf, psi.getAnonMap.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.MAP) with CMapType {

  override type Super = CMapType

  override def metaDeclPsi: Option[SchemaMetaDecl] = Option(psi.getMetaDecl)

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRef) // TODO check it's not a vartype?

  // `None` - no default, `Some(String)` - effective default tag name
  override def effectiveDefaultValueTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, _effectiveDefaultElementTagName
  )

  private lazy val _effectiveDefaultElementTagName: Option[String] = {
    linearizedParents.foreach { st =>
      if (!valueDataType.compatibleWith(st.valueDataType)) ctx.errors.add(
        CMessage.error(
          csf.filename,
          csf.position(psi.getAnonMap),
          s"Type `$name` inherits from map type `${st.name.name}` with different default value tag `${st.valueDataType.effectiveDefaultTagName.get}`"
        )
      )
    }
    valueDataType.effectiveDefaultTagName
  }

  /** Sanitized declared parents of this type in order of increasing priority. After [[CPhase.RESOLVE_TYPEREFS]]. */
  override def parents: Seq[CMapType] = ctx.getOrCreateAnonMapOf(keyTypeRef, valueDataType) +: super.parents

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getMapTypeBody).map(_.getAnnotationList)
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

//  override def isAssignableFrom(subtype: CType): Boolean =
//    subtype.kind == kind && elementTypeRef.resolved.isAssignableFrom(cast(subtype).elementTypeRef.resolved)

  override def supertypes: Seq[CAnonListType] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _supertypes)

  private lazy val _supertypes = parents.flatMap { st => st.supertypes :+ st }.distinct

  def parents: Seq[CAnonListType] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, _parents)

  private lazy val _parents: Seq[CAnonListType] = elementDataType.typeRef.resolved match {

    case et: CEntityTypeDef =>
      et.computeSupertypes(mutable.Stack())
      elementDataType.effectiveDefaultTagName match {
        case Some(tagName) => Seq(ctx.getOrCreateAnonListOf(et.dataType(None))) ++ et.parents.map(
          etp => ctx.getOrCreateAnonListOf(etp.dataType(etp.effectiveTags.find(_.name == tagName).map(_.name)))
        )
        case None => et.parents.map(etp => ctx.getOrCreateAnonListOf(etp.dataType(None)))
      }

    case et: CDatumType => et.parents.map(est => ctx.getOrCreateAnonListOf(est.dataType))

    case unknown => throw new UnsupportedOperationException(unknown.toString)

  }

  /** Immediate parents of this type in order of decreasing priority */
  override def linearizedParents: Seq[CAnonListType] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedParents)

  private lazy val _linearizedParents: Seq[CAnonListType] = {
    val linParents = elementDataType.typeRef.resolved match {
      case et: CEntityTypeDef => elementDataType.effectiveDefaultTagName.map { tagName =>
        if (!et.effectiveTags.exists(_.name == tagName)) ctx.errors.add(
          CMessage.error(csf.filename, CMessagePosition.NA, s"Tag `$tagName` is not defined for union type `${et.name.name}`")
        )
        ctx.getOrCreateAnonListOf(et.dataType(None))
      }.toSeq ++ et.linearizedParents.map { est => /* FIXME parent might not have the tag */
        ctx.getOrCreateAnonListOf(est.dataType(elementDataType.effectiveDefaultTagName))
      }
      case et: CDatumType => et.linearizedParents.map { est => ctx.getOrCreateAnonListOf(est.dataType) }
      case unknown => throw new UnsupportedOperationException(unknown.toString)
    }
    linParents foreach (_.linearizedParents) // trigger parents linearization
    linParents
  }

  final override def effectiveDefaultElementTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, elementDataType.effectiveDefaultTagName
  )

  override def metaDeclPsi: Option[SchemaMetaDecl] = None
}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends {

  override val elementDataType: CDataType = new CDataType(csf, psi.getAnonList.getValueTypeRef)

} with CTypeDef(csf, psi, CTypeKind.LIST) with CListType {

  override type Super = CListType

  override def metaDeclPsi: Option[SchemaMetaDecl] = Option(psi.getMetaDecl)

  // `None` - no default, `Some(String)` - effective default tag name
  override def effectiveDefaultElementTagName: Option[String] = ctx.after(
    CPhase.COMPUTE_SUPERTYPES, null, _effectiveDefaultElementTagName
  )

  private lazy val _effectiveDefaultElementTagName: Option[String] = {
    linearizedParents.foreach { st =>
      if (!elementDataType.compatibleWith(st.elementDataType)) ctx.errors.add(
        CMessage.error(
          csf.filename,
          csf.position(psi.getAnonList),
          s"Type `$name` inherits from list type `${st.name.name}` with different default element tag `${st.elementDataType.effectiveDefaultTagName.get}`"
        )
      )
    }
    elementDataType.effectiveDefaultTagName
  }

  /** Sanitized declared parents of this type in order of increasing priority. After [[CPhase.RESOLVE_TYPEREFS]]. */
  override def parents: Seq[CListType] = ctx.getOrCreateAnonListOf(elementDataType) +: super.parents

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getListTypeBody).map(_.getAnnotationList)
}


class CEnumTypeDef(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.ENUM
) with CDatumType {

  override type Super = CEnumTypeDef

  override def metaDeclPsi: Option[SchemaMetaDecl] = Option(psi.getMetaDecl)

  val values: Seq[CEnumValue] = {
    @Nullable val body = psi.getEnumTypeBody
    if (body == null) Nil else body.getEnumMemberDeclList.map(new CEnumValue(csf, _)).toList
  }

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getEnumTypeBody).map(_.getAnnotationList)
}

class CEnumValue(csf: CSchemaFile, psi: SchemaEnumMemberDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

}


class CPrimitiveTypeDef(csf: CSchemaFile, override val psi: SchemaPrimitiveTypeDef)(implicit ctx: CContext)
    extends CTypeDef(
      csf, psi, CTypeKind.forKeyword(psi.getPrimitiveTypeKind.name)
    ) with CDatumType {

  override type Super = CPrimitiveTypeDef

  override def metaDeclPsi: Option[SchemaMetaDecl] = Option(psi.getMetaDecl)

  override protected def annotationsPsiOpt: Option[util.List[SchemaAnnotation]] = Option(psi.getPrimitiveTypeBody).map(_.getAnnotationList)
}


class CSupplement(csf: CSchemaFile, val psi: SchemaSupplementDef)(implicit val ctx: CContext) {

  val sourceRef: CTypeDefRef = CTypeRef(csf, psi.sourceRef)

  val targetRefs: Seq[CTypeDefRef] = psi.supplementedRefs().map(CTypeRef(csf, _))

}

private [compiler] object CTypeUtil {

  def buildAnnotations(csf: CSchemaFile, psiOption: Option[java.util.List[SchemaAnnotation]])(implicit ctx: CContext): Annotations = psiOption.map {psi =>
    import JavaConverters._

    val ppc = new DefaultPsiProcessingContext
    val res = SchemaPsiParserUtil.parseAnnotations(
      psi,
      ppc,
      new ImportAwareTypesResolver(
        csf.namespace.fqn,
        csf.imports.values.map(i => i.fqn).toList.asJava,
        new CTypesResolver(csf)
      )
    )
    lazy val reporter = ErrorReporter.reporter(csf)
    ppc.messages().foreach{ e => reporter.error(e.message(), e.location()) }
    res
  }.getOrElse(Annotations.EMPTY)

}

object NamePatterns {

  val FieldName: Regex = """\p{javaLowerCase}(?:\p{IsAlphabetic}|\p{Digit})*""".r

  val TagName: Regex = FieldName

  val TypeName: Regex = """\p{javaUpperCase}(?:\p{IsAlphabetic}|\p{Digit})*""".r

  def validateFieldName(name: String, csf: CSchemaFile, psi: SchemaFieldDecl)(implicit ctx: CContext): String = {
    name match {
      case FieldName() =>
      case _ => ctx.errors.add(CMessage.error(csf.filename, csf.position(psi.getQid), s"Invalid field name `$name`"))
    }
    name
  }

  def validateTagName(name: String, csf: CSchemaFile, psi: SchemaEntityTagDecl)(implicit ctx: CContext): String = {
    name match {
      case TagName() =>
      case _ => ctx.errors.add(CMessage.error(csf.filename, csf.position(psi.getQid), s"Invalid tag name `$name`"))
    }
    name
  }

}
