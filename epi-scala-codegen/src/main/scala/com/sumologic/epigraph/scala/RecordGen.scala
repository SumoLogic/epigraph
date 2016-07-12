/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.scala

import com.sumologic.epigraph.schema.compiler._
import ScalaGenUtils._

object RecordGen extends ScalaGen {

  final override type From = CRecordTypeDef

  def generate(t: CRecordTypeDef): String =
    s"""
/*
 * Standard header
 */

package ${scalaFqn(t.name.fqn.removeLastSegment())}

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} RecordDatum[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with ImmRecordDatum[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with MutRecordDatum[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with RecordDatumBuilder[${baseName(t)}]

object ${objName(t)} extends RecordType[${baseName(t)}](
  namespace \\ "${baseName(t)}",
  Seq(${parentNames(t, objName)})
) {
  ${
      t.effectiveFieldsMap.values.map { f =>
        s"""
  val ${fieldName(f)}: ${fieldType(f, t)} = ${fieldDef(f, t)}
"""
      }.mkString("")
    }
  override def declaredFields: DeclaredFields = DeclaredFields(${t.declaredFields.map(fieldName).mkString(", ")})

  override def createMutable: ${mutName(t)} = new ${mutImplName(t)}

  private class ${mutImplName(t)} extends ${mutName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

  override def createBuilder: ${bldName(t)} = new ${bldImplName(t)}

  private class ${bldImplName(t)} extends ${bldName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

}
""".trim

  private def fieldName(f: CField): String = scalaName(f.name)

  private def fieldType(f: CField, ht: CTypeDef): String = s"DatumField[${ft(f, ht)}]" // TODO val _id: DatumField[FooId] = field("id", FooId)

  private def fieldDef(f: CField, ht: CTypeDef): String = s"""field("${f.name}", ${ft(f, ht)})""" // TODO val _id: DatumField[FooId] = field("id", FooId)

  private def ft(f: CField, ht: CTypeDef): String = s"${f.typeRef.resolved.name.name}" // TODO val _id: DatumField[FooId] = field("id", FooId)

}
