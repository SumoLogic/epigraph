/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.schema.compiler._
import com.sumologic.epigraph.schema.parser.Fqn

import scala.collection.GenTraversableOnce
import scala.collection.JavaConversions._

abstract class JavaGen[From >: Null <: AnyRef](protected val from: From, protected val ctx: CContext) {

  protected def relativeFilePath: Path

  protected def generate: String

  def writeUnder(sourcesRoot: Path): Unit = {
    GenUtils.writeFile(sourcesRoot, relativeFilePath, generate)
  }

  // TODO protected access for the rest:
  def javaName(name: String): String = if (JavaReserved.reserved.contains(name)) name + '_' else name

  def javaFqn(fqn: Fqn): String = fqn.segments.map(javaName).mkString(".")

  def localQName(typeFqn: Fqn, localNs: Fqn, trans: (String) => String = identity): Fqn = {
    val transLocal = trans(typeFqn.last())
    val ns = typeFqn.removeLastSegment()
    if (ns == localNs) new Fqn(transLocal) else ns.append(transLocal)
  }

  def ln(t: CTypeDef): String = t.name.local

  def javaLocalName(t: CTypeDef, trans: (String) => String): String = javaName(trans(ln(t)))

  def objName(s: String): String = s

  def objName(t: CTypeDef): String = javaLocalName(t, objName)

  def baseName(s: String): String = s

  def baseName(t: CTypeDef): String = javaLocalName(t, baseName)

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
    if (ctx.hasCollectionsOf(t)) collectionsInterface(t) + ", " else ""

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

  def bldName(t: CTypeDef): String = javaName(bldName(ln(t)))

  def bldImplName(s: String): String = bldName(s)// + ".Impl"

  def bldImplName(t: CTypeDef): String = javaLocalName(t, bldImplName)

  def javaQName(t: CTypeDef, ht: CTypeDef, trans: (String) => String = identity): String =
    javaFqn(localQName(t.name.fqn, ht.name.fqn.removeLastSegment(), trans))

  def baseQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, baseName)

  def immQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, immName)

  @Deprecated
  def mutQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, mutName)

  def bldQName(t: CTypeDef, ht: CTypeDef): String = javaQName(t, ht, bldName)

  def qnameArgs(fqn: Fqn): Seq[String] = fqn.last() +: fqn.removeLastSegment().segments.toSeq

  def withParents(t: CTypeDef, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(" " + javaQName(_, t, trans) + ",").mkString

  def parentNames(t: CTypeDef, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(javaQName(_, t, trans)).mkString(", ")

  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

}
