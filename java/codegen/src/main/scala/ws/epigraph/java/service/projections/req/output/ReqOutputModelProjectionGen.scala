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
import ws.epigraph.java.service.projections.req._
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqOutputModelProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  op: OpModelProjection[_, _, _ <: DatumTypeApi, _], // todo unused
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqOutputModelProjectionGen],
  protected val ctx: GenContext) extends ReqOutputTypeProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpModelProjection[_, _, _ <: DatumTypeApi, _]
  override type OpMetaProjectionType = OpModelProjection[_, _, _ <: DatumTypeApi, _]
  override type GenType = ReqOutputModelProjectionGen

//  referenceName.foreach(ref => ctx.reqOutputProjections.put(ref, this)) // todo rest

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ ln(cType) }$classNameSuffix"

//  override protected def reqVarProjectionFqn: Qn =
//    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputVarProjection")
//
//  override protected def reqModelProjectionFqn: Qn =
//    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"

  override protected lazy val flagged: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  /**
   * @return {@code true} if model is required
   */
  public boolean requried() {
    return raw.flagged();
  }
"""/*@formatter:on*/
  )

  override protected def metaGenerator(metaOp: OpMetaProjectionType): ReqOutputModelProjectionGen =
    ReqOutputModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      metaOp,
      Some(baseNamespace),
      namespaceSuffix.append("meta"),
      parentClassGenOpt.flatMap(_.metaGeneratorOpt.map(_.asInstanceOf[ReqOutputModelProjectionGen])),
      ctx
    )
}

object ReqOutputModelProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpModelProjection[_, _, _ <: DatumTypeApi, _],
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqOutputModelProjectionGen],
    ctx: GenContext): ReqOutputModelProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqOutputProjections,

      op.`type`().kind() match {

        case TypeKind.RECORD =>
          new ReqOutputRecordModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.MAP =>
          new ReqOutputMapModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.LIST =>
          new ReqOutputListModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqOutputPrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpPrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case x => throw new RuntimeException(s"Unsupported projection kind: $x")

      }

    )
}
