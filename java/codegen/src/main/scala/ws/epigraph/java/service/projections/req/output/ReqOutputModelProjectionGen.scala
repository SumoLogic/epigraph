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

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{CodeChunk, OperationInfo, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqOutputModelProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputModelProjection[_, _, _ <: DatumTypeApi],
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputProjectionGen(operationInfo, namespaceSuffix, ctx) {

  protected val cType: CDatumType  = ReqProjectionGen.toCType(op.model())

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  protected lazy val params: CodeChunk = ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")
}

object ReqOutputModelProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpOutputModelProjection[_,_,_ <: DatumTypeApi],
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
