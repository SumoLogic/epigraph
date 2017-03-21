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
import ws.epigraph.java.JavaGenNames.jn
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
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqDeleteProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpDeleteVarProjection
  override type OpTagProjectionEntryType = OpDeleteTagProjectionEntry

  override protected def name: Option[Qn] = Option(op.name())

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(name, _baseNamespace)

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(name, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpDeleteVarProjection, normalized: Boolean) =
    new ReqDeleteVarProjectionGen(
      operationInfo,
      op,
      baseNamespace,
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpDeleteVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(tpe: OpDeleteTagProjectionEntry): ReqProjectionGen =
    ReqDeleteModelProjectionGen.dataProjectionGen(
      None,
      operationInfo,
      tpe.projection(),
      baseNamespace,
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
    baseNamespace: Qn,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqDeleteProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqDeleteVarProjectionGen(operationInfo, op, baseNamespace, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqDeleteRecordModelProjectionGen(
        Option(op.name()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteRecordModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqDeleteMapModelProjectionGen(
        Option(op.name()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteMapModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqDeleteListModelProjectionGen(
        Option(op.name()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeleteListModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqDeletePrimitiveModelProjectionGen(
        Option(op.name()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpDeletePrimitiveModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
