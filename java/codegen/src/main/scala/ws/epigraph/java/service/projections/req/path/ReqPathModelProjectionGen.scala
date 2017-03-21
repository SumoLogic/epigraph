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

package ws.epigraph.java.service.projections.req.path

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.service.projections.req.path.ReqPathProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.path._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqPathModelProjectionGen(
  protected val operationInfo: OperationInfo,
  op: OpModelPath[_, _, _ <: DatumTypeApi],
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqPathProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpModelPath[_, _, _ <: DatumTypeApi]
  override type OpMetaProjectionType = OpModelPath[_, _, _ <: DatumTypeApi]

  override protected def name: Option[Qn] = None

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqVarPath")

  override protected def reqModelProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqModelPath")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"
}

object ReqPathModelProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpModelPath[_, _, _ <: DatumTypeApi],
    namespaceSuffix: Qn,
    ctx: GenContext): ReqPathModelProjectionGen = op.model().kind() match {

    case TypeKind.RECORD =>
      new ReqPathRecordModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpRecordModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqPathMapModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpMapModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqPathPrimitiveModelProjectionGen(
        operationInfo,
        op.asInstanceOf[OpPrimitiveModelPath],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unsupported path kind: $x")

  }
}
