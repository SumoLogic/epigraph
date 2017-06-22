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

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext, ObjectGenUtils}
import ws.epigraph.projections.op.OpParams

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpParamsGen(params: OpParams) extends ObjectGen[OpParams](params) {

  override protected def generateObject(ctx: ObjectGenContext): String =
    if (params.equals(OpParams.EMPTY)) "OpParams.EMPTY"
    else sn"""\
new OpParams(
  ${i(ObjectGenUtils.genVararg(params.asMap().values().map{p => gen(p, ctx)}, insertNewlines = true, ctx))}
)"""/*@formatter:on*/

}
