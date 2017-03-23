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

package ws.epigraph.java.service.projections.req.update

import ws.epigraph.java.GenContext
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqListModelProjectionGen, ReqModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.input.OpInputListModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateListModelProjectionGen(
  name: Option[Qn],
  operationInfo: OperationInfo,
  val op: OpInputListModelProjection,
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqUpdateModelProjectionGen(name, operationInfo, op, _baseNamespace, _namespaceSuffix, ctx) with ReqListModelProjectionGen {

  override type OpProjectionType = OpInputListModelProjection

  protected val elementGen: ReqUpdateProjectionGen = ReqUpdateVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    baseNamespace,
    namespaceSuffix.append(elementsNamespaceSuffix),
    ctx
  )

  override protected def tailGenerator(
    op: OpInputListModelProjection,
    normalized: Boolean): ReqModelProjectionGen =
    new ReqUpdateListModelProjectionGen(
      None,
      operationInfo,
      op,
      baseNamespace,
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpInputListModelProjection, ReqModelProjectionGen] = Map()
    }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateListModelProjection"),
    update
  )

}
