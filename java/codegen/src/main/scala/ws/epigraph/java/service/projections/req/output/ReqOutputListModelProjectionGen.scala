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

package ws.epigraph.java.service.projections.req.output

import ws.epigraph.java.GenContext
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqListModelProjectionGen, ReqModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputListModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputListModelProjectionGen(
  name: Option[Qn],
  operationInfo: OperationInfo,
  val op: OpOutputListModelProjection,
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqOutputModelProjectionGen(name, operationInfo, op, _baseNamespace, _namespaceSuffix, ctx) with ReqListModelProjectionGen {

  override type OpProjectionType = OpOutputListModelProjection

  protected val elementGen: ReqOutputProjectionGen = ReqOutputVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    baseNamespace,
    namespaceSuffix.append(elementsNamespaceSuffix),
    ctx
  )

  override protected def tailGenerator(
    op: OpOutputListModelProjection,
    normalized: Boolean): ReqModelProjectionGen =
    new ReqOutputListModelProjectionGen(
      None,
      operationInfo,
      op,
      baseNamespace,
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpOutputListModelProjection, ReqModelProjectionGen] = Map()
    }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputListModelProjection"),
    required
  )

}
