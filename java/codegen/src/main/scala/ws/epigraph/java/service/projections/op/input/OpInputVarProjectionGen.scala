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

package ws.epigraph.java.service.projections.op.input

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceGenUtils.{genLinkedMap, genList, genTag, genType}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.input.{OpInputTagProjectionEntry, OpInputVarProjection}
import ws.epigraph.types.TypeApi

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpInputVarProjectionGen(p: OpInputVarProjection)
  extends ServiceObjectGen[OpInputVarProjection](p) {

  override protected def generateObject(ctx: ServiceGenContext): String =
  /*@formatter:off*/sn"""\
new OpInputVarProjection(
  ${genType(null, p.`type`(), ctx)},
  ${i(genLinkedMap("String", "OpInputTagProjectionEntry", p.tagProjections().entrySet().map{e => ("\"" + e.getKey + "\"", genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
  ${p.parenthesized().toString},
  ${if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx)},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/

  private def genTagProjectionEntry(
    t: TypeApi,
    tpe: OpInputTagProjectionEntry,
    ctx: ServiceGenContext): String = {

    ctx.addImport(classOf[OpInputTagProjectionEntry].getName)

    /*@formatter:off*/sn"""\
new OpInputTagProjectionEntry(
  ${genTag(t, tpe.tag(), ctx)},
  ${i(gen(tpe.projection(), ctx))},
  ${gen(tpe.location(), ctx)}
)"""/*@formatter:on*/
  }
}
