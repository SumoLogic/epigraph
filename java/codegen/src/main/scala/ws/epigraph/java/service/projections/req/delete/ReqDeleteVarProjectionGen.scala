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
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, AbstractReqProjectionGen, ReqTypeProjectionGenCache, AbstractReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.delete._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqDeleteVarProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpDeleteVarProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqDeleteVarProjectionGen],
  protected val ctx: GenContext) extends ReqDeleteTypeProjectionGen with AbstractReqVarProjectionGen {

  override type OpProjectionType = OpDeleteVarProjection
  override type OpTagProjectionEntryType = OpDeleteTagProjectionEntry
  override protected type GenType = ReqDeleteVarProjectionGen

  override protected def baseNamespace: Qn = AbstractReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = AbstractReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpDeleteVarProjection, normalized: Boolean) =
    new ReqDeleteVarProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(this),
      ctx
    ) {
      override lazy val normalizedTailGenerators: Map[OpDeleteVarProjection, AbstractReqProjectionGen] = Map()
    }


  override protected def tagGenerator(
    pgo: Option[AbstractReqVarProjectionGen],
    tpe: OpDeleteTagProjectionEntry): AbstractReqProjectionGen =
    tagGenerator(
      tpe,
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqDeleteModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpDeleteTagProjectionEntry,
    parentTagGenOpt: Option[ReqDeleteModelProjectionGen]): AbstractReqProjectionGen =
    ReqDeleteModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteVarProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteTagProjectionEntry")
  )
}

object ReqDeleteVarProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpDeleteVarProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqDeleteTypeProjectionGen],
    ctx: GenContext): ReqDeleteTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqDeleteProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqDeleteVarProjectionGen(
            baseNamespaceProvider,
            op,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteVarProjectionGen]),
            ctx
          )
        case TypeKind.RECORD =>
          new ReqDeleteRecordModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpDeleteRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteModelProjectionGen]),
            ctx
          )
        case TypeKind.MAP =>
          new ReqDeleteMapModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpDeleteMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteModelProjectionGen]),
            ctx
          )
        case TypeKind.LIST =>
          new ReqDeleteListModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpDeleteListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteModelProjectionGen]),
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqDeletePrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpDeletePrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteModelProjectionGen]),
            ctx
          )
        case x => throw new RuntimeException(s"Unknown projection kind: $x")

      }

    )
}
