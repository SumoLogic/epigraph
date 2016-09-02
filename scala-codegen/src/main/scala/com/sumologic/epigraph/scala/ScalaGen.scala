/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.scala

import java.nio.file.Path

import com.sumologic.epigraph.schema.compiler._
import io.epigraph.lang.Fqn

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

  def scalaFqn(fqn: Fqn): String = fqn.segments.map(scalaName).mkString(".")

  def localQName(typeFqn: Fqn, localNs: Fqn, trans: (String) => String = identity): Fqn = {
    val transLocal = trans(typeFqn.last())
    val ns = typeFqn.removeLastSegment()
    if (ns == localNs) new Fqn(transLocal) else ns.append(transLocal)
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
    t.getLinearizedParentsReversed.map(" " + scalaQName(_, t, trans) + " with").mkString

  def parentNames(t: CTypeDef, trans: (String) => String = identity): String =
    t.getLinearizedParentsReversed.map(scalaQName(_, t, trans)).mkString(", ")

  def ?(arg: AnyRef, ifNotNull: => String, ifNull: => String): String = if (arg ne null) ifNotNull else ifNull

  def ?(arg: GenTraversableOnce[_], ifNotNull: => String, ifNull: => String): String =
    if (arg != null && arg.nonEmpty) ifNotNull else ifNull

  def ?[A >: Null <: AnyRef, B](arg: A, ifNotNull: => B, ifNull: => B): B = if (arg ne null) ifNotNull else ifNull

}
