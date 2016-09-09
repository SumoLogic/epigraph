/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.sumologic.epigraph.util.JavaFunction
import io.epigraph.lang.Fqn

import scala.collection.JavaConversions._


class CContext(val tabWidth: Int = 2) {

  private var _phase: CPhase = CPhase.PARSE

  /** Accumulated compile errors */
  val errors: ConcurrentLinkedQueue[CError] = new java.util.concurrent.ConcurrentLinkedQueue

  /** Schema files being processed by compiler */
  val schemaFiles: ConcurrentHashMap[String, CSchemaFile] = new java.util.concurrent.ConcurrentHashMap

  /** Accumulated namespaces */
  val namespaces: ConcurrentHashMap[String, CNamespace] = new java.util.concurrent.ConcurrentHashMap

  // TODO: split typeDefs into data type defs and var type defs?
  val typeDefs: ConcurrentHashMap[CTypeName, CTypeDef] = new java.util.concurrent.ConcurrentHashMap

  //val anonListTypes: ConcurrentHashMap[CAnonListTypeName, CAnonListType] = new java.util.concurrent.ConcurrentHashMap
  val anonListTypes: ConcurrentHashMap[CDataType, CAnonListType] = new java.util.concurrent.ConcurrentHashMap

  //val anonMapTypes: ConcurrentHashMap[CAnonMapTypeName, CAnonMapType] = new java.util.concurrent.ConcurrentHashMap
  val anonMapTypes: ConcurrentHashMap[(CTypeRef, CDataType), CAnonMapType] = new java.util.concurrent.ConcurrentHashMap

  /** Types implicitly imported (unless superseeded by explicit import statement) by every schema file */
  val implicitImports: Map[String, Fqn] = Seq(
    "epigraph.String",
    "epigraph.Integer",
    "epigraph.Long",
    "epigraph.Double",
    "epigraph.Boolean"
  ).map(Fqn.fromDotSeparated).map { fqn => (fqn.last, fqn) }.toMap

  def phase: CPhase = _phase

  def phase(next: CPhase): this.type = {
    assert(phase.ordinal == next.ordinal || next.ordinal == phase.ordinal + 1, phase.name)
    _phase = next
    this
  }

  def after[A](prevPhase: CPhase, default: A, f: => A): A = if (phase.ordinal <= prevPhase.ordinal) default else f

  def getOrCreateAnonListOf(elementDataType: CDataType): CAnonListType =
    anonListTypes.computeIfAbsent(elementDataType, AnonListTypeConstructor)

  private val AnonListTypeConstructor = JavaFunction[CDataType, CAnonListType](new CAnonListType(_)(this))

  def getOrCreateAnonMapOf(keyTypeRef: CTypeRef, valueDataType: CDataType): CAnonMapType =
    anonMapTypes.computeIfAbsent((keyTypeRef, valueDataType), AnonMapTypeConstructor)

  private val AnonMapTypeConstructor =
    JavaFunction[(CTypeRef, CDataType), CAnonMapType] { case (kt, vt) => new CAnonMapType(kt, vt)(this) }

}

case class CError(filename: String, position: CErrorPosition, message: String) {

}

case class CErrorPosition(line: Int, column: Int, lineText: Option[String])

object CErrorPosition {

  val NA: CErrorPosition = CErrorPosition(0, 0, None)

}
