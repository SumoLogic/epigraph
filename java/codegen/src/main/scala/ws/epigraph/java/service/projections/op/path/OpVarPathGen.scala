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

package ws.epigraph.java.service.projections.op.path

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.ObjectGenUtils.{genTagExpr, genTypeExpr}
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext}
import ws.epigraph.projections.op.path.{OpTagPath, OpVarPath}
import ws.epigraph.types.TypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpVarPathGen(p: OpVarPath) extends ObjectGen[OpVarPath](p) {

  override protected def generateObject(ctx: ObjectGenContext): String = {

    /*@formatter:off*/sn"""\
new OpVarPath(
  ${genTypeExpr(p.`type`(), ctx.gctx)},
  ${i(genTagPath(p.`type`(), p.singleTagProjection(), ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/

  }

  private def genTagPath(
    t: TypeApi,
    tp: OpTagPath,
    ctx: ObjectGenContext): String = {

    if (tp == null) "null"
    else {
      ctx.addImport(classOf[OpTagPath].getName)

      /*@formatter:off*/sn"""\
new OpTagPath(
  ${genTagExpr(t, tp.tag().name(), ctx.gctx)},
  ${i(gen(tp.projection(), ctx))},
  ${gen(tp.location(), ctx)}
)"""/*@formatter:on*/

    }
  }
}
