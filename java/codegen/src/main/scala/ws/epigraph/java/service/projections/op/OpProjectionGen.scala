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

package ws.epigraph.java.service.projections.op

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, sp}
import ws.epigraph.java.ObjectGenUtils._
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext}
import ws.epigraph.projections.op.OpProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class OpProjectionGen[P <: OpProjection[_, _]](p: P) extends ObjectGen[P](p) {
  override protected def generateObject(
    o: String,
    ctx: ObjectGenContext): String = {

    val opName = p.referenceName()
    if (opName != null) {
      val opNameString = opName.toString
      val visitedKey = "projections.op." + opNameString
      val methodName = "constructProjectionFor$" + opNameString.replace(".", "_")

      if (!ctx.visited(visitedKey)) {
        ctx.addVisited(visitedKey)

        ctx.use("java.util.Map")
        ctx.use("java.util.HashMap")

        if (ctx.addField(s"private static Map<String, $o> projectionRefs = new HashMap<>();"))
          ctx.addStatic("projectionRefs = null;")
        ctx.addMethod(
          /*@formatter:off*/sn"""\
private static $o $methodName() {
  $o ref = projectionRefs.get("$opNameString");
  if (ref != null && ref.isResolved()) return ref;
  if (ref == null) {
    ref = new $o(
      ${genTypeExpr(p.`type`(), ctx.gctx)},
      ${gen(p.location(), ctx)}
    );
    projectionRefs.put("$opNameString", ref);
    $o value = ${sp(4, generateNonVisitedObject(o, ctx))};
    ref.resolve(${gen(opName, ctx)}, value);
  }
  return ref;
}"""/*@formatter:on*/
        )
      }

      s"$methodName()"
    } else {
      generateNonVisitedObject(o, ctx)
    }

  }

  protected def generateNonVisitedObject(objExpr: String, ctx: ObjectGenContext): String
}
