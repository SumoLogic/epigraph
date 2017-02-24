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
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{CodeChunk, OperationInfo, ReqModelProjectionGen, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqOutputModelProjectionGen(
  protected val operationInfo: OperationInfo,
  op: OpOutputModelProjection[_, _, _ <: DatumTypeApi],
  protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqOutputProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpOutputModelProjection[_, _, _ <: DatumTypeApi]
  override type OpMetaProjectionType = OpOutputModelProjection[_, _, _ <: DatumTypeApi]

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputVarProjection")

  override protected def reqModelProjectionFqn: Qn =
  Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"

  protected lazy val required: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  public boolean requried() {
    return raw.required();
  }
"""/*@formatter:on*/)

  override protected def metaGenerator(metaOp: OpMetaProjectionType): ReqProjectionGen =
    ReqOutputModelProjectionGen.dataProjectionGen(
      operationInfo,
      metaOp,
      namespaceSuffix.append("meta"),
      ctx
    )
}

object ReqOutputModelProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpOutputModelProjection[_, _, _ <: DatumTypeApi],
    namespaceSuffix: Qn,
    ctx: GenContext): ReqOutputModelProjectionGen = op.model().kind() match {

    case TypeKind.RECORD =>
      new ReqOutputRecordModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpOutputRecordModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqOutputMapModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpOutputMapModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqOutputListModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpOutputListModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqOutputPrimitiveModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpOutputPrimitiveModelProjection],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unsupported projection kind: $x")

  }
}
