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
import ws.epigraph.java.service.ServiceGenUtils.genTypeExpr
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.path.OpMapModelPath
import ws.epigraph.types.{MapType, TypeApi}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpMapModelPathGen(p: OpMapModelPath)
  extends ServiceObjectGen[OpMapModelPath](p) {

  override protected def generateObject(ctx: ServiceGenContext): String = {
    ctx.addImport(classOf[MapType].getName)

    /*@formatter:off*/sn"""\
new OpMapModelPath(
  ${genTypeExpr(p.`type`().asInstanceOf[TypeApi], ctx.gctx)},
  ${i(gen(p.params(), ctx))},
  ${i(gen(p.annotations(), ctx))},
  ${i(gen(p.keyProjection(), ctx))},
  ${i(gen(p.itemsProjection(), ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/
  }
}
