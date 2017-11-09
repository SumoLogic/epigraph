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
  override protected def generateObject(o: String, ctx: ObjectGenContext): String = datum match {
    case rd: GRecordDatum => generateRecord(o, rd, ctx)
    case md: GMapDatum => generateMap(o, md, ctx)
    case ld: GListDatum => generateList(o, ld, ctx)
    case pd: GPrimitiveDatum => generatePrimitive(o, pd, ctx)
    case ed: GEnumDatum => generateEnum(o, ed, ctx)
    case nd: GNullDatum => generateNull(o, nd, ctx)
    case _ => throw new IllegalArgumentException("Unknown data type: " + datum.getClass.getName)
  }

  private def generateRecord(o: String, rd: GRecordDatum, ctx: ObjectGenContext): String = {
    val gdv = ctx.use(classOf[GDataValue].getCanonicalName)
    /*@formatter:off*/sn"""\
new $o(
  ${gen(rd.typeRef(), ctx)},
  ${i(ObjectGenUtils.genLinkedMap("java.lang.String", gdv.toString, rd.fields().entrySet().toList.map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(rd.location(), ctx)}
)"""/*@formatter:on*/
  }

  private def generateMap(o: String, md: GMapDatum, ctx: ObjectGenContext): String = {
    val gdv = ctx.use(classOf[GDataValue].getCanonicalName)
    /*@formatter:off*/sn"""\
new $o(
  ${gen(md.typeRef(), ctx)},
  ${i(ObjectGenUtils.genLinkedMap(o, gdv.toString, md.entries().entrySet().toList.map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(md.location(), ctx)}
)"""/*@formatter:on*/
  }

  private def generateList(o: String, ld: GListDatum, ctx: ObjectGenContext): String =
  /*@formatter:off*/sn"""\
new $o(
  ${gen(ld.typeRef(), ctx)},
  ${i(ObjectGenUtils.genList(ld.values().map(gen(_, ctx)), ctx))},
  ${gen(ld.location(), ctx)}
)"""/*@formatter:on*/

  private def generatePrimitive(o: String, pd: GPrimitiveDatum, ctx: ObjectGenContext): String =
    s"new $o(${gen(pd.typeRef(), ctx)}, ${gen(pd.value(), ctx)}, ${gen(pd.location(), ctx)})"

  private def generateEnum(o: String, ed: GEnumDatum, ctx: ObjectGenContext): String =
    s"""new $o("${ed.value()}", ${gen(ed.location(), ctx)})"""

  private def generateNull(o: String, nd: GNullDatum, ctx: ObjectGenContext): String =
    s"new $o(${gen(nd.typeRef(), ctx)}, ${gen(nd.location(), ctx)})"
}
