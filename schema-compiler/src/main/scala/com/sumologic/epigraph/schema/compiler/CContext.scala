/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.sumologic.epigraph.schema.parser.Fqn
import com.sumologic.epigraph.util.JavaFunction

import scala.collection.JavaConversions._


class CContext(val tabWidth: Int = 2) {

  private var _phase: CPhase = CPhase.PARSE

  val errors: ConcurrentLinkedQueue[CError] = new java.util.concurrent.ConcurrentLinkedQueue

  val schemaFiles: ConcurrentHashMap[String, CSchemaFile] = new java.util.concurrent.ConcurrentHashMap

  val namespaces: ConcurrentHashMap[String, CNamespace] = new java.util.concurrent.ConcurrentHashMap

  // TODO: split typeDefs into data type defs and var type defs?
  val typeDefs: ConcurrentHashMap[CTypeName, CTypeDef] = new java.util.concurrent.ConcurrentHashMap

  val anonListTypes: ConcurrentHashMap[CAnonListTypeName, CAnonListType] = new java.util.concurrent.ConcurrentHashMap

  val anonMapTypes: ConcurrentHashMap[CAnonMapTypeName, CAnonMapType] = new java.util.concurrent.ConcurrentHashMap

  val implicitImports: Map[String, Fqn] = Seq(
    "epigraph.String",
    "epigraph.Integer",
    "epigraph.Long",
    "epigraph.Double",
    "epigraph.Boolean"
  ).map(Fqn.fromDotSeparated).map { fqn => (fqn.last, fqn) }.toMap

  // TODO private val AnonListTypeConstructor = JavaFunction[CAnonListTypeName, CAnonListType](new CAnonListType(_))

  // TODO def getOrCreateAnonListType(elementType: CType) = anonListTypes.computeIfAbsent(elementType.name, AnonListTypeConstructor)

  def hasAnonListOf(elementType: CType): Boolean =
    anonListTypes.keySet().exists(_.elementTypeRef.resolved == elementType)

  def getAnonListOf(elementType: CType): Option[CAnonListType] =
    anonListTypes.entrySet().find(_.getKey.elementTypeRef.resolved == elementType).map(_.getValue)

  def hasAnonMapOf(valueType: CType): Boolean =
    anonMapTypes.keySet().exists(_.valueTypeRef.resolved == valueType)

  def getAnonMapsOf(valueType: CType): Seq[CAnonMapType] = // TODO do we need this one?
    anonMapTypes.entrySet().filter(_.getKey.valueTypeRef.resolved == valueType).map(_.getValue)(collection.breakOut)

  def getAnonMapOf(keyType: CType, valueType: CType): Option[CAnonMapType] =
    anonMapTypes.entrySet().find(entry =>
      entry.getKey.keyTypeRef.resolved == keyType && entry.getKey.valueTypeRef.resolved == valueType
    ).map(_.getValue)

  def hasCollectionsOf(elementType: CType): Boolean = hasAnonListOf(elementType) || hasAnonMapOf(elementType)

  def after[A](prevPhase: CPhase, default: A, f: => A): A = if (phase.ordinal <= prevPhase.ordinal) default else f

  def phase: CPhase = _phase

  def phase(next: CPhase): this.type = {
    assert(phase.ordinal == next.ordinal || next.ordinal == phase.ordinal + 1, phase.name)
    _phase = next
    this
  }

}

case class CError(filename: String, position: CErrorPosition, message: String) {

}

case class CErrorPosition(line: Int, column: Int, lineText: Option[String])

object CErrorPosition {

  val NA: CErrorPosition = CErrorPosition(0, 0, None)

}

//class Phased[A](val after: CPhase, default: A)(val f: () => A) {
//
//  private lazy val a: A =
//
//}