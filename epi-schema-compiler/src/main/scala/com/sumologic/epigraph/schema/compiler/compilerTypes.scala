/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.sumologic.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.mutable


trait CType {

  val name: CTypeName

  val kind: CTypeKind

  def isAssignableFrom(subtype: CType): Boolean

}


abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit val ctx: CContext) extends CType {self =>

  type Same >: this.type <: CTypeDef {type Same <: self.Same}

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

  def supertypes: Seq[Same] = ctx.after(CPhase.RESOLVE_TYPEREFS, null, computedSupertypes.getOrElse(Nil))

  private var computedSupertypes: Option[Seq[Same]] = None

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
              new CError(
                csf.filename, csf.position(psi.getQid), // TODO use injection source/position for injection failures
                s"Type ${name.name} (of '${kind.keyword}' kind) is not compatible with $pSource supertype ${p.name.name} (of `${p.kind.keyword}` kind)"
              )
            )
          }
        }
        computedSupertypes = Some(
          declaredAndInjectedParents.flatMap { st => st.supertypes :+ st }.distinct.asInstanceOf[Seq[Same]]
        )
      } else {
        ctx.errors.add(
          new CError(
            csf.filename, csf.position(psi.getQid),
            s"Cyclic inheritance: type ${visited.view(0, thisIdx + 2).reverseIterator.map(_.name.name).mkString(" < ")}"
          )
        )
        computedSupertypes = Some(Nil)
      }
      visited.pop()
    }
  }

  def parents: Seq[Same] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, cachedParents)

  private lazy val cachedParents: Seq[Same] = (declaredSupertypeRefs.map(_.resolved) ++ injectedSupertypes)
      .asInstanceOf[Seq[Same]]

//  def depth: Int = ctx.phased(CPhase.INHERIT_FROM_SUPERTYPES, -1, cachedDepth)
//
//  private lazy val cachedDepth: Int = if (supertypes.isEmpty) 0 else supertypes.map(_.depth).max + 1
//
//  def depthSortedSupertypes: Seq[Same] = ctx.phased(CPhase.INHERIT_FROM_SUPERTYPES, Nil, cachedDepthSortedSupertypes)
//
//  private lazy val cachedDepthSortedSupertypes = computedSupertypes.get.sortBy(_.depth).asInstanceOf[Seq[Same]]
//
//  def declaredAndInjectedParents: Seq[Same] =
//    (declaredSupertypeRefs.map(_.resolved) ++ injectedSupertypes).asInstanceOf[Seq[Same]]
//
//  def linearize: Seq[Same] = this.asInstanceOf[Same] +: declaredAndInjectedParents.foldLeft[Seq[Same]](Nil) { (acc, p) =>
//     p.linearize.filterNot(acc.contains).asInstanceOf[Seq[Same]] ++ acc
//  }

  def linearizedParents: Seq[Same] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, cachedLinearizedParents)

  private lazy val cachedLinearizedParents: Seq[Same] = parents.foldLeft[Seq[Same]](Nil) { (acc, p) =>
    if (acc.contains(p) || parents.exists(_.supertypes.contains(p))) acc else p +: acc
  }

  def linearizedSupertypes: Seq[Same] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedSupertypes)

  private lazy val _linearizedSupertypes: Seq[Same] = parents.foldLeft[Seq[Same]](Nil) { (acc, p) =>
    p.linearized.filterNot(acc.contains) ++ acc
  }

  def linearized: Seq[Same] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearized)

  private lazy val _linearized: Seq[Same] = this.asInstanceOf[this.type] +: linearizedSupertypes

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

}


class CVarTypeDef(csf: CSchemaFile, override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.VARTYPE
) {

  override type Same = CVarTypeDef

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredTagsMap: Map[String, CTag] = declaredTags.map { ct => (ct.name, ct) }(collection.breakOut)

  def effectiveTagsMap: Map[String, CTag] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveTagsMap)

  private lazy val _effectiveTagsMap = {
    (for {vt <- linearizedParents; nt <- vt.effectiveTagsMap} yield nt) ++ declaredTagsMap
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
              new CError(
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
              new CError(
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


class CRecordTypeDef(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.RECORD
) {

  override type Same = CRecordTypeDef

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredFieldsMap: Map[String, CField] = declaredFields.map { ct => (ct.name, ct) }(collection.breakOut)

  def effectiveFieldsMap: Map[String, CField] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _effectiveFieldsMap)

  private lazy val _effectiveFieldsMap = {
    (for {vt <- linearizedParents; nt <- vt.effectiveFieldsMap} yield nt) ++ declaredFieldsMap
  } groupBy { case (n, _t) =>
    n
  } map { case (n, seq) =>
    (n, seq.map { case (_n, t) => t })
  } map { case (n, seq) =>
    (n, effectiveField(declaredFieldsMap.get(n), seq))
  }

  private def effectiveField(declaredFieldOpt: Option[CField], superfields: Seq[CField]): CField = {
    declaredFieldOpt match {
      case Some(df) => // check if declared field is compatible with all (if any) overridden ones
        superfields foreach { st =>
          if (!df.compatibleWith(st)) {
            ctx.errors.add(
              new CError(
                csf.filename, csf.position(df.psi),
                s"Type `${
                  df.typeRef.resolved.name.name
                }` of field `${df.name}` is not a subtype of its parent field type `${
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
            narrowest
          case None =>
            ctx.errors.add(
              new CError(
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

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  def compatibleWith(sf: CField): Boolean = sf.typeRef.resolved.isAssignableFrom(typeRef.resolved)

//  lazy val defaultTag: Option[CTag] = {
//    val sdo = psi.getDefaultOverride
//    if (sdo == null || sdo.getNodefault != null) {
//      None
//    } else {
//      val tagName = sdo.getVarTagRef.getQid.getCanonicalName
//      typeRef.getTypeOpt match {
//        case Some(ctype) =>
//          ctype match {
//            case cvt: CVarTypeDef => cvt.allTags... // FIXME get all tags, find referenced, error if not found
//            case _ => //TODO error
//          }
//        case None => None
//      }
//    }
//  }

}


trait CMapType extends CType {

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueTypeRef: CTypeRef

  override val kind: CTypeKind = CTypeKind.MAP

  protected def cast(ctype: CType): CMapType = ctype.asInstanceOf[CMapType]

}

class CAnonMapType(override val name: CAnonMapTypeName) extends CMapType {

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override val valueTypeRef: CTypeRef = name.valueTypeRef

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind &&
        keyTypeRef.resolved == subtype.asInstanceOf[CMapType].keyTypeRef.resolved &&
        valueTypeRef.resolved.isAssignableFrom(cast(subtype).valueTypeRef.resolved)

}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.MAP
) with CMapType {

  override type Same = CMapTypeDef

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRefList.head)

  override val valueTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRefList.last)

}


trait CListType extends CType {

  val name: CTypeName

  val elementTypeRef: CTypeRef

  override val kind: CTypeKind = CTypeKind.LIST

  protected def cast(ctype: CType): CListType = ctype.asInstanceOf[CListType]

}


class CAnonListType(override val name: CAnonListTypeName) extends CListType {

  override val elementTypeRef: CTypeRef = name.elementTypeRef

  override def isAssignableFrom(subtype: CType): Boolean =
    subtype.kind == kind &&
        elementTypeRef.resolved.isAssignableFrom(cast(subtype).elementTypeRef.resolved)

}


class CListTypeDef(csf: CSchemaFile, override val psi: SchemaListTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.LIST
) with CListType {

  override type Same = CListTypeDef

  override val elementTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonList.getTypeRef)

}


class CEnumTypeDef(csf: CSchemaFile, psi: SchemaEnumTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.ENUM
) {

  override type Same = CEnumTypeDef

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
      csf, psi, CTypeKind.forKeyword( // TODO deal with all nulls?
        Seq(psi.getStringT, psi.getIntegerT, psi.getLongT, psi.getDoubleT, psi.getBooleanT).find(_ != null).get.getText
      )
    ) {

  override type Same = CPrimitiveTypeDef

}


class CSupplement(csf: CSchemaFile, val psi: SchemaSupplementDef)(implicit val ctx: CContext) {

  val sourceRef: CTypeDefRef = CTypeRef(csf, psi.sourceRef)

  val targetRefs: Seq[CTypeDefRef] = psi.supplementedRefs().map(CTypeRef(csf, _))

}
