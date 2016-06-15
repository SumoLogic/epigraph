/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import net.jcip.annotations.ThreadSafe


class CContext(val tabWidth: Int = 2) {

  @ThreadSafe
  val errors: ConcurrentLinkedQueue[CError] = new java.util.concurrent.ConcurrentLinkedQueue[CError]

  @ThreadSafe
  val types: ConcurrentHashMap[CTypeName, CType] = new java.util.concurrent.ConcurrentHashMap[CTypeName, CType]

}

case class CError(filename: String, position: CErrorPosition, message: String) {

}

case class CErrorPosition(line: Int, column: Int, lineText: Option[String])

object CErrorPosition {

  val NA: CErrorPosition = CErrorPosition(0, 0, None)

}
