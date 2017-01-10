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

package ws.epigraph.java.service.projections.op.output

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceGenUtils._
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.output.{OpOutputTagProjectionEntry, OpOutputVarProjection}
import ws.epigraph.types.TypeApi

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpOutputVarProjectionGen(p: OpOutputVarProjection)
  extends ServiceObjectGen[OpOutputVarProjection](p) {

  override protected def generateObject(ctx: ServiceGenContext): String = {

    if (ctx.generateSeparateMethodsForVarProjections) {
      val methodName = "constructOutputVarProjection" + ctx.nextMethodUID

      ctx.addMethod(
        /*@formatter:off*/sn"""\
private static OpOutputVarProjection $methodName() {
  return new OpOutputVarProjection(
    ${genTypeExpr(p.`type`(), ctx.gctx)},
    ${i(genLinkedMap("String", "OpOutputTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
      (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
    ${p.parenthesized().toString},
    ${if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx)},
    ${gen(p.location(), ctx)}
  );
}"""/*@formatter:on*/
      )

      s"$methodName()"

    } else {

      /*@formatter:off*/sn"""\
new OpOutputVarProjection(
  ${genTypeExpr(p.`type`(), ctx.gctx)},
  ${i(genLinkedMap("String", "OpOutputTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
    (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
  ${p.parenthesized().toString},
  ${if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx)},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/

    }

  }

  private def genTagProjectionEntry(
    t: TypeApi,
    tpe: OpOutputTagProjectionEntry,
    ctx: ServiceGenContext): String = {

    ctx.addImport(classOf[OpOutputTagProjectionEntry].getName)

    /*@formatter:off*/sn"""\
new OpOutputTagProjectionEntry(
  ${genTagExpr(t, tpe.tag().name(), ctx.gctx)},
  ${i(gen(tpe.projection(), ctx))},
  ${gen(tpe.location(), ctx)}
)"""/*@formatter:on*/
  }
}
