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

package ws.epigraph.java.service.projections.op.delete

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceGenUtils._
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.delete.{OpDeleteTagProjectionEntry, OpDeleteVarProjection}
import ws.epigraph.types.TypeApi

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpDeleteVarProjectionGen(p: OpDeleteVarProjection) extends ServiceObjectGen[OpDeleteVarProjection](p) {

  override protected def generateObject(ctx: ServiceGenContext): String = {

    val opName = p.name()
    if (opName != null) {
      val opNameString = p.name().toString

      val visitedKey = "projections.op.output.delete." + opNameString
      val methodName = "constructDeleteVarProjectionFor$" + opNameString.replace(".", "_")

      if (!ctx.visited(visitedKey)) {

        ctx.addVisited(visitedKey)
        ctx.addImport("java.util.Map")
        ctx.addImport("java.util.HashMap")

        if (ctx.addField("private static Map<String, OpDeleteVarProjection> deleteProjectionRefs = new HashMap<>();"))
           ctx.addStatic("deleteProjectionRefs = null;")

        ctx.addMethod(
          /*@formatter:off*/sn"""\
private static OpDeleteVarProjection $methodName() {
  OpDeleteVarProjection ref = deleteProjectionRefs.get("$opNameString");
  if (ref != null && ref.isResolved()) return ref;
  if (ref == null) {
    ref = new OpDeleteVarProjection(
      ${genTypeExpr(p.`type`(), ctx.gctx)},
      ${gen(p.location(), ctx)}
    );
    deleteProjectionRefs.put("$opNameString", ref);
    OpDeleteVarProjection value = new OpDeleteVarProjection(
      ${genTypeExpr(p.`type`(), ctx.gctx)},
      ${gen(p.canDelete, ctx)},
      ${i(genLinkedMap("String", "OpDeleteTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
        (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
      ${p.parenthesized().toString},
      ${i(if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx))},
      ${gen(p.location(), ctx)}
    );
    ref.resolve(${gen(opName, ctx)}, value);
  }
  return ref;
}"""/*@formatter:on*/
        )

      }

      s"$methodName()"

    } else {

      /*@formatter:off*/sn"""\
new OpDeleteVarProjection(
  ${genTypeExpr(p.`type`(), ctx.gctx)},
  ${gen(p.canDelete, ctx)},
  ${i(genLinkedMap("String", "OpDeleteTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
    (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
  ${p.parenthesized().toString},
  ${i(if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/

    }

  }

  private def genTagProjectionEntry(
    t: TypeApi,
    tpe: OpDeleteTagProjectionEntry,
    ctx: ServiceGenContext): String = {

    ctx.addImport(classOf[OpDeleteTagProjectionEntry].getName)

    /*@formatter:off*/sn"""\
new OpDeleteTagProjectionEntry(
  ${genTagExpr(t, tpe.tag().name(), ctx.gctx)},
  ${i(gen(tpe.projection(), ctx))},
  ${gen(tpe.location(), ctx)}
)"""/*@formatter:on*/
  }
}
