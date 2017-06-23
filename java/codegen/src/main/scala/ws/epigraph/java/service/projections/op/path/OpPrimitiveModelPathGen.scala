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
import ws.epigraph.java.ObjectGenUtils.genTypeExpr
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext}
import ws.epigraph.projections.op.path.OpPrimitiveModelPath
import ws.epigraph.types.{PrimitiveType, TypeApi}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpPrimitiveModelPathGen(p: OpPrimitiveModelPath) extends ObjectGen[OpPrimitiveModelPath](p) {

  override protected def generateObject(ctx: ObjectGenContext): String = {
    ctx.use(classOf[PrimitiveType[_]].getName)

    /*@formatter:off*/sn"""\
new OpInputPrimitiveModelProjection(
  ${genTypeExpr(p.`type`().asInstanceOf[TypeApi], ctx.gctx)},
  ${i(gen(p.params(), ctx))},
  ${i(gen(p.annotations(), ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/
  }
}
