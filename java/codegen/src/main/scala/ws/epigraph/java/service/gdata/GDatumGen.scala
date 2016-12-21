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

package ws.epigraph.java.service.gdata

import ws.epigraph.gdata._
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceGenUtils, ServiceObjectGen}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class GDatumGen(datum: GDatum) extends ServiceObjectGen[GDatum](datum) {
  override protected def generateObject(ctx: ServiceGenContext): String = datum match {
    case rd: GRecordDatum => generateRecord(rd, ctx)
    case md: GMapDatum => generateMap(md, ctx)
    case ld: GListDatum => generateList(ld, ctx)
    case pd: GPrimitiveDatum => generatePrimitive(pd, ctx)
    case ed: GEnumDatum => generateEnum(ed, ctx)
    case nd: GNullDatum => generateNull(nd, ctx)
    case _ => throw new IllegalArgumentException("Unknown data type: " + datum.getClass.getName)
  }

  private def generateRecord(rd: GRecordDatum, ctx: ServiceGenContext): String =
  /*@formatter:off*/sn"""\
new GenMapDatum(
  ${gen(rd.typeRef(), ctx)},
  ${i(ServiceGenUtils.genLinkedMap("String", "GDataValue", rd.fields().entrySet().map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(rd.location(), ctx)}
)"""/*@formatter:on*/

  private def generateMap(md: GMapDatum, ctx: ServiceGenContext): String =
  /*@formatter:off*/sn"""\
new GenMapDatum(
  ${gen(md.typeRef(), ctx)},
  ${i(ServiceGenUtils.genLinkedMap("GDatum", "GDataValue", md.entries().entrySet().map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(md.location(), ctx)}
)"""/*@formatter:on*/

  private def generateList(ld: GListDatum, ctx: ServiceGenContext): String =
    /*@formatter:off*/sn"""\
new GenListDatum(
  ${gen(ld.typeRef(), ctx)},
  ${i(ServiceGenUtils.genList(ld.values().map(gen(_, ctx)), ctx))},
  ${gen(ld.location(), ctx)}
)"""/*@formatter:on*/

  private def generatePrimitive(pd: GPrimitiveDatum, ctx: ServiceGenContext): String =
    s"new GPrimitiveDatum(${gen(pd.typeRef(), ctx)}, ${gen(pd.value(), ctx)}, ${gen(pd.location(), ctx)})"

  private def generateEnum(ed: GEnumDatum, ctx: ServiceGenContext): String =
    s"""new GEnumDatum("${ed.value()}", ${gen(ed.location(), ctx)})"""

  private def generateNull(nd: GNullDatum, ctx: ServiceGenContext): String =
    s"new GNullDatum(${gen(nd.typeRef(), ctx)}, ${gen(nd.location(), ctx)})"
}
