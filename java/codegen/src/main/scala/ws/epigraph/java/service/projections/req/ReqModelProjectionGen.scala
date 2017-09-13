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

package ws.epigraph.java.service.projections.req

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.ReqProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqModelProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  op: OpOutputModelProjection[_, _, _ <: DatumTypeApi, _], // todo unused
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqModelProjectionGen],
  protected val ctx: GenContext) extends ReqTypeProjectionGen with AbstractReqModelProjectionGen {

  override type OpProjectionType <: OpOutputModelProjection[_, _, _ <: DatumTypeApi, _]
  override type OpMetaProjectionType = OpOutputModelProjection[_, _, _ <: DatumTypeApi, _]
  override type GenType = ReqModelProjectionGen

//  referenceName.foreach(ref => ctx.reqOutputProjections.put(ref, this)) // todo rest

  override protected def baseNamespace: Qn = AbstractReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = AbstractReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ ln(cType) }$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqEntityProjection")

  override protected def reqModelProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"

  protected lazy val flagged: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  public boolean flagged() { return raw.flagged(); }
"""/*@formatter:on*/
  )

  override protected def metaGenerator(metaOp: OpMetaProjectionType): ReqModelProjectionGen =
    ReqModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      metaOp,
      Some(baseNamespace),
      namespaceSuffix.append("meta"),
      parentClassGenOpt.flatMap(_.metaGeneratorOpt.map(_.asInstanceOf[ReqModelProjectionGen])),
      ctx
    )
}

object ReqModelProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpOutputModelProjection[_, _, _ <: DatumTypeApi, _],
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqModelProjectionGen],
    ctx: GenContext): ReqModelProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqOutputProjections,

      op.`type`().kind() match {

        case TypeKind.RECORD =>
          new ReqRecordModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpOutputRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.MAP =>
          new ReqMapModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpOutputMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.LIST =>
          new ReqListModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpOutputListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqPrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpOutputPrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case x => throw new RuntimeException(s"Unsupported projection kind: $x")

      }

    )
}
