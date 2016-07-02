/* Created by yegor on 6/10/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.sumologic.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._
import scala.collection.mutable


trait CType {

  //type Same >: this.type <: CType { type Same <: this.Same}

  val name: CTypeName

  val kind: CTypeKind

}


trait CSupertyped[+T <: CSupertyped[T]] {this: T =>

  def ctx: CContext

  /** declared and injected parents */
  def parents: Seq[T]

  def supertypes: Seq[T]

  def linearizedParents: Seq[T] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, cachedLinearizedParents)

  private lazy val cachedLinearizedParents: Seq[T] = parents.foldLeft[Seq[T]](Nil) { (acc, p) =>
    if (acc.contains(p) || parents.exists(_.supertypes.contains(p))) acc else p +: acc
  }

  def linearizedSupertypes: Seq[T]= ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearizedSupertypes)

  private lazy val _linearizedSupertypes: Seq[T] = parents.foldLeft[Seq[T]](Nil) { (acc, p) =>
    p.linearized.filterNot(acc.contains) ++ acc
  }

  def linearized: Seq[T] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _linearized)

  private lazy val _linearized: Seq[T] = this +: linearizedSupertypes

}

abstract class CTypeDef protected(val csf: CSchemaFile, val psi: SchemaTypeDef, override val kind: CTypeKind)
    (implicit val ctx: CContext) extends CType with CSupertyped[CTypeDef] {

  type Same >: this.type <: CTypeDef

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
                s"Type ${name.name} (of '${kind.keyword}' kind) is not compatible with $pSource supertype ${p.name.name} (of '${p.kind.keyword}' kind)"
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

}

object CTypeDef {

  def apply(csf: CSchemaFile, stdw: SchemaTypeDefWrapper)(implicit ctx: CContext): CTypeDef = {
    val std: SchemaTypeDef = stdw.getElement
    val ctype = std match {
      case vt: SchemaVarTypeDef => new CVarTypeDef(csf, vt)
      case rt: SchemaRecordTypeDef => new CRecordTypeDef(csf, rt)
      case mt: SchemaMapTypeDef => new CMapTypeDef(csf, mt)
      case lt: SchemaListTypeDef => new CListTypeDef(csf, lt)
      case et: SchemaEnumTypeDef => new CEnumTypeDef(csf, et)
      case pt: SchemaPrimitiveTypeDef => new CPrimitiveTypeDef(csf, pt)
      case _ => null
    }
    ctype
  }

}


class CVarTypeDef(csf: CSchemaFile, override val psi: SchemaVarTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.VARTYPE
) with CSupertyped[CVarTypeDef] {

  override type Same = CVarTypeDef

  val declaredTags: Seq[CTag] = {
    @Nullable val body = psi.getVarTypeBody
    if (body == null) Nil else body.getVarTagDeclList.map(new CTag(csf, _)).toList
  }

  // TODO check for dupes
  private val declaredTagsMap: Map[String, CTag] = declaredTags.map { ct => (ct.name, ct) }(collection.breakOut)

  def supertagsMap: Map[String, Seq[CTag]] = {
    for {vt <- linearizedSupertypes; nt <- vt.declaredTagsMap} yield nt
  } groupBy { case (n, _t) =>
    n
  } map { case (n, seq) =>
    (n, seq.map { case (_n, t) => t })
  } filter { case (n, seq) =>
    seq.tail.nonEmpty
  }

  def effectiveTag(declaredTagOpt: Option[CTag], supertags: Seq[CTag]): CTag = {
    declaredTagOpt match {
      case Some(dt) =>
        supertags foreach { st => /* check dt compat with st */}
        ???
      case None =>
        ???
    }
  }

  /** supertags with different origins that might need to be overridden (if they are not compatible with each other) */
  def supertagsToOverride: Map[String, Seq[CTag]] = ctx.after(CPhase.COMPUTE_SUPERTYPES, null, _supertagsToOverride)

  private lazy val _supertagsToOverride: Map[String, Seq[CTag]] = {
    for {vt <- parents; nt <- vt.declaredTagsMap} yield nt
  } groupBy { case (n, _t) =>
    n
  } map { case (n, seq) =>
    (n, seq.map { case (_n, t) => t })
  } filter { case (n, seq) =>
    seq.tail.nonEmpty
  }

  def allTags: Map[String, Seq[CVarTypeDef]] = ctx.phased(CPhase.INHERIT_FROM_SUPERTYPES, Map.empty, cachedAllTags)

  private def cachedAllTags: Map[String, Seq[CVarTypeDef]] = linearized flatMap { vt =>
    vt.declaredTags map { dt =>
      (dt.name, vt)
    }
  } groupBy { case (dtn, _vt) => dtn } map { case (dtn, seq) => (dtn, seq.map { case (_dtn, vt) => vt }) }

}

class CTag(val csf: CSchemaFile, val psi: SchemaVarTagDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

  val overriddenTags: Seq[CTag] = Nil // FIXME

}


class CRecordTypeDef(csf: CSchemaFile, override val psi: SchemaRecordTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.RECORD
) {

  override type Same = CRecordTypeDef

  val declaredFields: Seq[CField] = {
    @Nullable val body = psi.getRecordTypeBody
    if (body == null) Nil else body.getFieldDeclList.map(new CField(csf, _)).toList
  }

}

class CField(val csf: CSchemaFile, val psi: SchemaFieldDecl)(implicit val ctx: CContext) {

  val name: String = psi.getQid.getCanonicalName

  val typeRef: CTypeRef = CTypeRef(csf, psi.getTypeRef)

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

  //override type Same >: this.type <: CMapType

  val name: CTypeName

  val keyTypeRef: CTypeRef

  val valueTypeRef: CTypeRef

  override val kind: CTypeKind = CTypeKind.MAP

}

class CAnonMapType(override val name: CAnonMapTypeName) extends CMapType {

  override val keyTypeRef: CTypeRef = name.keyTypeRef

  override val valueTypeRef: CTypeRef = name.valueTypeRef

}

class CMapTypeDef(csf: CSchemaFile, override val psi: SchemaMapTypeDef)(implicit ctx: CContext) extends CTypeDef(
  csf, psi, CTypeKind.MAP
) with CMapType {

  override type Same = CMapTypeDef

  override val keyTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRefList.head)

  override val valueTypeRef: CTypeRef = CTypeRef(csf, psi.getAnonMap.getTypeRefList.last)

}


trait CListType extends CType {

  //override type Same >: this.type <: CListType

  val name: CTypeName

  val elementTypeRef: CTypeRef

  override val kind: CTypeKind = CTypeKind.LIST

}

class CAnonListType(override val name: CAnonListTypeName) extends CListType {

  override val elementTypeRef: CTypeRef = name.elementTypeRef

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
