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
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqPrimitiveModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputPrimitiveModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputPrimitiveModelProjectionGen(
  operationInfo: OperationInfo,
  protected val op: OpOutputPrimitiveModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) with ReqPrimitiveModelProjectionGen {

  override type OpProjectionType = OpOutputPrimitiveModelProjection

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputPrimitiveModelProjection"),
    required
  )

}
