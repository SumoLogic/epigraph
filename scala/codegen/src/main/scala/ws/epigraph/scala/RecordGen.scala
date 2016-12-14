/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 7/11/16. */

package ws.epigraph.scala

import ws.epigraph.edl.compiler._

class RecordGen(from: CRecordTypeDef) extends TypeScalaGen[CRecordTypeDef](from) {

  protected def generate: String = s"""
/*
 * Standard header
 */

package ${scalaFqn(t.name.fqn.removeLastSegment())}

import ws.epigraph.xp.data._
import ws.epigraph.xp.data.builders._
import ws.epigraph.xp.data.immutable._
import ws.epigraph.xp.data.mutable._
import ws.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} RecordDatum[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with ImmRecordDatum[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with MutRecordDatum[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with RecordDatumBuilder[${baseName(t)}]

object ${objName(t)} extends RecordType[${baseName(t)}](namespace \\ "${baseName(t)}", Seq(${parentNames(t, objName)})) {
  ${
    t.effectiveFields.map { f => s"""
  val ${fieldName(f)}: ${fieldType(f, t)} = ${fieldDef(f, t)}\n"""
    }.mkString
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

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def fieldType(f: CField, ht: CTypeDef): String = s"DatumField[${ft(f, ht)}]"

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def fieldDef(f: CField, ht: CTypeDef): String = s"""field("${f.name}", ${ft(f, ht)})"""

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def ft(f: CField, ht: CTypeDef): String = s"${f.typeRef.resolved.name.name}"

}
