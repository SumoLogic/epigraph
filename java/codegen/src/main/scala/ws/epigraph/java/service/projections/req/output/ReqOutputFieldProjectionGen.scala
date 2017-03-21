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
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqFieldProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputFieldProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputFieldProjectionGen(
  protected val operationInfo: OperationInfo,
  fieldName: String,
  protected val op: OpOutputFieldProjection,
  baseNamespaceOpt: Option[Qn],
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqOutputProjectionGen with ReqFieldProjectionGen {

  override type OpFieldProjectionType = OpOutputFieldProjection

  override protected def baseNamespace: Qn = baseNamespaceOpt.getOrElse(super.baseNamespace)

  override val shortClassName: String = s"$classNamePrefix${ up(fieldName) }Field$classNameSuffix"

  override lazy val dataProjectionGen: ReqOutputProjectionGen =
    ReqOutputVarProjectionGen.dataProjectionGen(
      operationInfo,
      op.varProjection(),
      baseNamespace,
      namespaceSuffix,
      ctx
    )

  override protected def generate: String = generate(
    fieldName, Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputFieldProjection")
  )

}
