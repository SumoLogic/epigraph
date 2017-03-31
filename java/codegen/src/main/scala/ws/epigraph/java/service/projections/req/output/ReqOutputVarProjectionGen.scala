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
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen, ReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.ProjectionReferenceName
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputVarProjectionGen(
  protected val operationInfo: OperationInfo,
  protected val op: OpOutputVarProjection,
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqOutputProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpOutputVarProjection
  override type OpTagProjectionEntryType = OpOutputTagProjectionEntry

  override protected def referenceName: Option[ProjectionReferenceName] = Option(op.referenceName())

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(referenceName, _baseNamespace)

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceName, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpOutputVarProjection, normalized: Boolean) =
    new ReqOutputVarProjectionGen(
      operationInfo,
      op,
      baseNamespace,
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpOutputVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(tpe: OpOutputTagProjectionEntry): ReqProjectionGen =
    ReqOutputModelProjectionGen.dataProjectionGen(
      None, // can't use named projections in tags yet
      operationInfo,
      tpe.projection(),
      baseNamespace,
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputVarProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry")
  )
}

object ReqOutputVarProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpOutputVarProjection,
    baseNamespace: Qn,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqOutputProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqOutputVarProjectionGen(operationInfo, op, baseNamespace, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqOutputRecordModelProjectionGen(
        Option(op.referenceName()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqOutputMapModelProjectionGen(
        Option(op.referenceName()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqOutputListModelProjectionGen(
        Option(op.referenceName()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqOutputPrimitiveModelProjectionGen(
        Option(op.referenceName()),
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
