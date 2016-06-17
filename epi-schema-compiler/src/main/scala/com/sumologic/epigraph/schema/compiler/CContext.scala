/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.sumologic.epigraph.schema.parser.Fqn
import net.jcip.annotations.ThreadSafe


class CContext(val tabWidth: Int = 2) {

  @ThreadSafe
  val errors: ConcurrentLinkedQueue[CError] = new java.util.concurrent.ConcurrentLinkedQueue

  @ThreadSafe
  val typeDefs: ConcurrentHashMap[CTypeName, CTypeDef] = new java.util.concurrent.ConcurrentHashMap

  @ThreadSafe
  val anonListTypes: ConcurrentHashMap[CAnonListTypeName, CAnonListType] = new java.util.concurrent.ConcurrentHashMap

  @ThreadSafe
  val anonMapTypes: ConcurrentHashMap[CAnonMapTypeName, CAnonMapType] = new java.util.concurrent.ConcurrentHashMap


  val implicitImports: Map[String, Fqn] = Seq(
    "epigraph.String",
    "epigraph.Integer",
    "epigraph.Long",
    "epigraph.Double",
    "epigraph.Boolean"
  ).map(Fqn.fromDotSeparated).map { fqn => (fqn.last, fqn) }.toMap

}

case class CError(filename: String, position: CErrorPosition, message: String) {

}

case class CErrorPosition(line: Int, column: Int, lineText: Option[String])

object CErrorPosition {

  val NA: CErrorPosition = CErrorPosition(0, 0, None)

}
