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
import ws.epigraph.java.service.ServiceGenUtils.{genTypeExpr,genList}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceObjectGen}
import ws.epigraph.projections.op.delete.OpDeleteListModelProjection
import ws.epigraph.types.{ListType, TypeApi}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpDeleteListModelProjectionGen(p: OpDeleteListModelProjection)
  extends ServiceObjectGen[OpDeleteListModelProjection](p) {

  override protected def generateObject(ctx: ServiceGenContext): String = {
    ctx.addImport(classOf[ListType].getName)

    /*@formatter:off*/sn"""\
new OpDeleteListModelProjection(
  ${genTypeExpr(p.`type`().asInstanceOf[TypeApi], ctx.gctx)},
  ${i(gen(p.params(), ctx))},
  ${i(gen(p.annotations(), ctx))},
  ${i(gen(p.itemsProjection(), ctx))},
  ${i(if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/
  }
}
