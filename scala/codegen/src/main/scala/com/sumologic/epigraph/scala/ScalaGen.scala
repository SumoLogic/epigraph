/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.scala

import java.nio.file.Path

import com.sumologic.epigraph.schema.compiler._
import io.epigraph.lang.Qn

import scala.collection.GenTraversableOnce
import scala.collection.JavaConversions._
import scala.meta.Term

abstract class ScalaGen[From >: Null <: AnyRef](protected val from: From) {

  protected def relativeFilePath: Path

  protected def generate: String

  def writeUnder(sourcesRoot: Path): Unit = {
    ScalaGenUtils.writeFile(sourcesRoot, relativeFilePath, generate)
  }

  // TODO protected access for the rest:
  def scalaName(name: String): String = Term.Name(name).toString





  /** scala package name for given type */
  def pn(t: CType): String = getNamedTypeComponent(t).name.fqn.removeLastSegment().segments.map(scalaName).mkString(".")

  /** scala type name for given type as seen from the context of the other type namespace */
  def lqn(t: CType, lt: CType, lnTrans: (String) => String = identity): String = {
    val tpn = pn(t)
    if (tpn == pn(lt)) lnTrans(ln(t)) else tpn + "." + lnTrans(ln(t))
  }

  /** local (short) scala name for given type */
  def ln(t: CType): String = t match {
    case t: CTypeDef => t.name.local
    case t: CAnonListType => alln(t)
    case t: CAnonMapType => amln(t)
  }

  def alln(t: CAnonListType): String = t.elementDataType.typeRef.resolved match {
    case et: CVarTypeDef => ln(et) + varTagPart(t.elementDataType.effectiveDefaultTagName) + "_List"
    case et: CDatumType => ln(et) + "_List"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  def amln(t: CAnonMapType): String = t.valueDataType.typeRef.resolved match {
    case vt: CVarTypeDef => ln(t.keyTypeRef.resolved) + "_" + ln(vt) + varTagPart(t.valueDataType.effectiveDefaultTagName) + "_Map"
    case vt: CDatumType => ln(t.keyTypeRef.resolved) + "_" + ln(vt) + "_Map"
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }

  private def varTagPart(tagName: Option[String]): String = tagName match {
    case Some(name) => "$" + name
    case None => ""
  }

  protected def getNamedTypeComponent(t: CType): CTypeDef = t match {
    case td: CTypeDef => td
    case alt: CAnonListType => getNamedTypeComponent(alt.elementTypeRef.resolved)
    case amt: CAnonMapType => getNamedTypeComponent(amt.valueTypeRef.resolved)
    case unknown => throw new UnsupportedOperationException(unknown.toString)
  }





  def scalaFqn(fqn: Qn): String = fqn.segments.map(scalaName).mkString(".")

  def localQName(typeFqn: Qn, localNs: Qn, trans: (String) => String = identity): Qn = {
    val transLocal = trans(typeFqn.last())
    val ns = typeFqn.removeLastSegment()
    if (ns == localNs) new Qn(transLocal) else ns.append(transLocal)
  }

  def ln(t: CTypeDef): String = t.name.local

  def scalaLocalName(t: CTypeDef, trans: (String) => String): String = scalaName(trans(ln(t)))

  def objName(s: String): String = s

  def objName(t: CTypeDef): String = scalaLocalName(t, objName)

  def baseName(s: String): String = s

  def baseName(t: CTypeDef): String = scalaLocalName(t, baseName)

  def immName(s: String): String = s + "Imm"

  def immName(t: CTypeDef): String = scalaLocalName(t, immName)

  def mutName(s: String): String = s + "Mut"

  def mutName(t: CTypeDef): String = scalaLocalName(t, mutName)

  def mutImplName(s: String): String = mutName(s) + "Impl"

  def mutImplName(t: CTypeDef): String = scalaLocalName(t, mutImplName)

  def bldName(s: String): String = s + "Bld"

  def bldName(t: CTypeDef): String = scalaName(bldName(ln(t)))

  def bldImplName(s: String): String = bldName(s) + "Impl"

  def bldImplName(t: CTypeDef): String = scalaLocalName(t, bldImplName)

  def scalaQName(t: CTypeDef, ht: CTypeDef, trans: (String) => String = identity): String =
    scalaFqn(localQName(t.name.fqn, ht.name.fqn.removeLastSegment(), trans))

  def baseQName(t: CTypeDef, ht: CTypeDef): String = scalaQName(t, ht, baseName)

  def immQName(t: CTypeDef, ht: CTypeDef): String = scalaQName(t, ht, immName)

  def mutQName(t: CTypeDef, ht: CTypeDef): String = scalaQName(t, ht, mutName)

  def bldQName(t: CTypeDef, ht: CTypeDef): String = scalaQName(t, ht, bldName)

  def withParents(t: CTypeDef, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(" " + lqn(_, t, trans) + " with").mkString

  def parentNames(t: CTypeDef, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(lqn(_, t, trans)).mkString(", ")

  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

}
