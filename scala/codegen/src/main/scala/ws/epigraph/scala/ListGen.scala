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

/* Created by yegor on 7/12/16. */

package ws.epigraph.scala

import ws.epigraph.schema.compiler.CListTypeDef

class ListGen(from: CListTypeDef) extends TypeScalaGen[CListTypeDef](from) {

  protected override def generate: String = s"""
/*
 * Standard header
 */

package ${scalaFqn(t.name.fqn.removeLastSegment())}

import ws.epigraph.xp.data._
import ws.epigraph.xp.data.immutable._
import ws.epigraph.xp.data.mutable._
import ws.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} ListDatum[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with ImmListDatum[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with MutListDatum[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with ListDatumBuilder[${baseName(t)}]

object ${objName(t)} extends ListType[${baseName(t)}](namespace \\ "${baseName(t)}", Seq(${parentNames(t, objName)})) { // TODO add anonymous list to parents?

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

}
