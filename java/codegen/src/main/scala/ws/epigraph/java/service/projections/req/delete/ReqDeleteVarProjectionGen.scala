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
import ws.epigraph.java.JavaGenNames.{jn, ln}
import ws.epigraph.java.service.projections.req.delete.ReqDeleteProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen, ReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.delete._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqDeleteVarProjectionGen(
  protected val operationInfo: OperationInfo,
  protected val op: OpDeleteVarProjection,
  protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqDeleteProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpDeleteVarProjection
  override type OpTagProjectionEntryType = OpDeleteTagProjectionEntry

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def tailGenerator(op: OpDeleteVarProjection, normalized: Boolean) =
    new ReqDeleteVarProjectionGen(
      operationInfo,
      op,
      namespaceSuffix.append(
        ReqVarProjectionGen.typeNameToPackageName(cType, namespace.toString) + ReqVarProjectionGen.tailPackageSuffix(
          normalized)
      ),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpDeleteVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(tpe: OpDeleteTagProjectionEntry): ReqProjectionGen =
    ReqDeleteModelProjectionGen.dataProjectionGen(
      operationInfo,
      tpe.projection(),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteVarProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteTagProjectionEntry")
  )
}

object ReqDeleteVarProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpDeleteVarProjection,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqDeleteProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqDeleteVarProjectionGen(operationInfo, op, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqDeleteRecordModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteRecordModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqDeleteMapModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteMapModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqDeleteListModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteListModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqDeletePrimitiveModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeletePrimitiveModelProjection],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
