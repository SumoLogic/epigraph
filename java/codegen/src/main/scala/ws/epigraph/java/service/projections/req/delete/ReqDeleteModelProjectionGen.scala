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

package ws.epigraph.java.service.projections.req.delete

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.service.projections.req.delete.ReqDeleteProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqModelProjectionGen, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.ProjectionReferenceName
import ws.epigraph.projections.op.delete._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqDeleteModelProjectionGen(
  protected val referenceName: Option[ProjectionReferenceName],
  protected val operationInfo: OperationInfo,
  op: OpDeleteModelProjection[_, _, _ <: DatumTypeApi],
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqDeleteProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpDeleteModelProjection[_, _, _ <: DatumTypeApi]

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(referenceName, _baseNamespace)

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceName, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteVarProjection")

  override protected def reqModelProjectionFqn: Qn =
  Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"
}

object ReqDeleteModelProjectionGen {
  def dataProjectionGen(
    name: Option[ProjectionReferenceName],
    operationInfo: OperationInfo,
    op: OpDeleteModelProjection[_, _, _ <: DatumTypeApi],
    baseNamespace: Qn,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqDeleteModelProjectionGen = op.`type`().kind() match {

    case TypeKind.RECORD =>
      new ReqDeleteRecordModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpDeleteRecordModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqDeleteMapModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpDeleteMapModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqDeleteListModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpDeleteListModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqDeletePrimitiveModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpDeletePrimitiveModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unsupported projection kind: $x")

  }
}
