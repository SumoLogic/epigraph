/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.schema.compiler._
import io.epigraph.lang.schema.parser.Fqn

import scala.collection.GenTraversableOnce
import scala.collection.JavaConversions._

abstract class JavaGen[From >: Null <: AnyRef](protected val from: From, protected val ctx: CContext) {

  protected def relativeFilePath: Path

  protected def generate: String

  def writeUnder(sourcesRoot: Path): Unit = {
    GenUtils.writeFile(sourcesRoot, relativeFilePath, generate)
  }

  // TODO protected access for the rest:

  /** java identifier name (https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8) */
  def jn(name: String): String = if (JavaReserved.reserved.contains(name)) name + '_' else name

  def javaFqn(fqn: Fqn): String = fqn.segments.map(jn).mkString(".")

  /** local (short) java name for given type */
  def ln(t: CType): String = t match {
    case t: CTypeDef => t.name.local
    case t: CAnonListType => ln(t.elementTypeRef.resolved) + "_List"
    case t: CAnonMapType => ln(t.valueTypeRef.resolved) + "_Map"
  }

  protected def getNamedTypeComponent(t: CType): CTypeDef = t match {
    case td: CTypeDef => td
    case alt: CAnonListType => getNamedTypeComponent(alt.elementTypeRef.resolved)
    case amt: CAnonMapType => getNamedTypeComponent(amt.valueTypeRef.resolved)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def javaLocalName(t: CType, trans: (String) => String = identity): String = jn(trans(ln(t)))

  def objName(s: String): String = s

  def objName(t: CTypeDef): String = javaLocalName(t, objName)

  def baseName(s: String): String = s

  def baseName(t: CType): String = javaLocalName(t, baseName)

  def baseValueName(s: String): String = s + ".Value"

  def baseValueName(t: CTypeDef): String = javaLocalName(t, baseValueName)

  def baseDataName(s: String): String = s + ".Data"

  def baseDataName(t: CTypeDef): String = javaLocalName(t, baseDataName)

  def elementName(t: CType): String = t match {
    case alt: CAnonListType =>
      elementName(alt.elementTypeRef.resolved) + ".List"
    case amt: CAnonMapType =>
      elementName(amt.valueTypeRef.resolved) + ".Map<" + elementName(amt.keyTypeRef.resolved) + ".Imm>"
    case td: CTypeDef =>
      baseName(td)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def withCollections(t: CType): String =
    if (ctx.hasCollectionsOf(t)) " " + collectionsInterface(t) + "," else ""

  def collectionsInterface(t: CType): String = typeComponents(t).mkString("_") + "_Collections"

  def typeComponents(t: CType): Seq[String] = t match {
    case alt: CAnonListType => typeComponents(alt.elementTypeRef.resolved) :+ "List"
    case amt: CAnonMapType => typeComponents(amt.valueTypeRef.resolved) :+ "Map"
    case td: CTypeDef => Seq(baseName(td))
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def immName(s: String): String = s + ".Imm"

  def immName(t: CTypeDef): String = javaLocalName(t, immName)

  @Deprecated
  def mutName(s: String): String = s + ".Builder"

  @Deprecated
  def mutName(t: CTypeDef): String = javaLocalName(t, mutName)

  @Deprecated
  def mutImplName(s: String): String = mutName(s) + ".Impl"

  @Deprecated
  def mutImplName(t: CTypeDef): String = javaLocalName(t, mutImplName)

  def bldName(s: String): String = s + ".Builder"

  def bldName(t: CTypeDef): String = jn(bldName(ln(t)))

  def bldImplName(s: String): String = bldName(s)// + ".Impl"

  def bldImplName(t: CTypeDef): String = javaLocalName(t, bldImplName)

  /** java type name for given type as seen from the context of the other type namespace */
  def lqn(t: CType, lt: CType, lnTrans: (String) => String = identity): String = {
    val tpn = pn(t)
    if (tpn == pn(lt)) lnTrans(ln(t)) else tpn + "." + ln(t)
  }

  /** java type name for given typeref as seen from the context of the other type namespace */
  def lqrn(tr: CTypeRef, lt: CType, lnTrans: (String) => String = identity): String = lqn(tr.resolved, lt, lnTrans)

  /** default tag type for given typeref and default tag name */
  def dtt(tr: CTypeRef, dtn: String): CType = tr.resolved match {
    case vt: CVarTypeDef => vt.effectiveTagsMap(dtn).typeRef.resolved
    case dt: CDatumType => dt
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  /** java package name for given type */
  def pn(t: CType): String = getNamedTypeComponent(t).name.fqn.removeLastSegment().segments.map(jn).mkString(".")

  @Deprecated
  def javaQName(t: CType, ht: CType, trans: (String) => String = identity): String = javaFqn(
    localQName(getNamedTypeComponent(t).name.fqn, getNamedTypeComponent(ht).name.fqn.removeLastSegment(), trans)
  )

  def localQName(typeFqn: Fqn, localNs: Fqn, trans: (String) => String = identity): Fqn = {
    val transLocal = trans(typeFqn.last()) // FIXME use ln(t) here
    val ns = typeFqn.removeLastSegment()
    if (ns == localNs) new Fqn(transLocal) else ns.append(transLocal)
  }

  @Deprecated
  def baseQName(t: CType, ht: CType): String = javaQName(t, ht, baseName)

  def immQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, immName)

  @Deprecated
  def mutQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, mutName)

  def bldQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, bldName)

  def qnameArgs(fqn: Fqn): Seq[String] = fqn.last() +: fqn.removeLastSegment().segments.toSeq

  def withParents(t: CType, trans: (String) => String = identity): String = {
    t.getLinearizedParentsReversed.map(" " + lqn(_, t, trans) + ",").mkString
  }

  def parentNames(t: CType, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(javaQName(_, t, trans)).mkString(", ")

  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

}
