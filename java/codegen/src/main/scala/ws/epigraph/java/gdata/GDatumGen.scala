/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.java.gdata

import ws.epigraph.gdata._
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.ObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext, ObjectGenUtils}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class GDatumGen(datum: GDatum) extends ObjectGen[GDatum](datum) {
  override protected def generateObject(ctx: ObjectGenContext): String = datum match {
    case rd: GRecordDatum => generateRecord(rd, ctx)
    case md: GMapDatum => generateMap(md, ctx)
    case ld: GListDatum => generateList(ld, ctx)
    case pd: GPrimitiveDatum => generatePrimitive(pd, ctx)
    case ed: GEnumDatum => generateEnum(ed, ctx)
    case nd: GNullDatum => generateNull(nd, ctx)
    case _ => throw new IllegalArgumentException("Unknown data type: " + datum.getClass.getName)
  }

  private def generateRecord(rd: GRecordDatum, ctx: ObjectGenContext): String = {
    ctx.addImport(classOf[GDataValue].getCanonicalName)
    /*@formatter:off*/sn"""\
new GRecordDatum(
  ${gen(rd.typeRef(), ctx)},
  ${i(ObjectGenUtils.genLinkedMap("String", "GDataValue", rd.fields().entrySet().map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(rd.location(), ctx)}
)"""/*@formatter:on*/
  }

  private def generateMap(md: GMapDatum, ctx: ObjectGenContext): String = {
    ctx.addImport(classOf[GDataValue].getCanonicalName)
    /*@formatter:off*/sn"""\
new GMapDatum(
  ${gen(md.typeRef(), ctx)},
  ${i(ObjectGenUtils.genLinkedMap("GDatum", "GDataValue", md.entries().entrySet().map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(md.location(), ctx)}
)"""/*@formatter:on*/
  }

  private def generateList(ld: GListDatum, ctx: ObjectGenContext): String =
  /*@formatter:off*/sn"""\
new GListDatum(
  ${gen(ld.typeRef(), ctx)},
  ${i(ObjectGenUtils.genList(ld.values().map(gen(_, ctx)), ctx))},
  ${gen(ld.location(), ctx)}
)"""/*@formatter:on*/

  private def generatePrimitive(pd: GPrimitiveDatum, ctx: ObjectGenContext): String =
    s"new GPrimitiveDatum(${gen(pd.typeRef(), ctx)}, ${gen(pd.value(), ctx)}, ${gen(pd.location(), ctx)})"

  private def generateEnum(ed: GEnumDatum, ctx: ObjectGenContext): String =
    s"""new GEnumDatum("${ed.value()}", ${gen(ed.location(), ctx)})"""

  private def generateNull(nd: GNullDatum, ctx: ObjectGenContext): String =
    s"new GNullDatum(${gen(nd.typeRef(), ctx)}, ${gen(nd.location(), ctx)})"
}
