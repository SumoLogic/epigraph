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
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqModelProjectionGen, ReqProjectionGen, ReqTypeProjectionGenCache}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqDeleteModelProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  op: OpModelProjection[_, _, _ <: DatumTypeApi, _],
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqDeleteModelProjectionGen],
  protected val ctx: GenContext) extends ReqDeleteTypeProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpModelProjection[_, _, _ <: DatumTypeApi, _]
  override type GenType = ReqDeleteModelProjectionGen

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ ln(cType) }$classNameSuffix"

}

object ReqDeleteModelProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpModelProjection[_, _, _ <: DatumTypeApi, _],
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqDeleteModelProjectionGen],
    ctx: GenContext): ReqDeleteModelProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      ctx.reqDeleteProjections,

      op.`type`().kind() match {

        case TypeKind.RECORD =>
          new ReqDeleteRecordModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.MAP =>
          new ReqDeleteMapModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.LIST =>
          new ReqDeleteListModelProjectionGen(
            baseNamespaceProvider,
            op.asInstanceOf[OpListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt,
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqDeletePrimitiveModelProjectionGen(
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
