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
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqEntityProjectionGen, ReqProjectionGen, ReqTypeProjectionGenCache}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqDeleteEntityProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpEntityProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqDeleteEntityProjectionGen],
  protected val ctx: GenContext) extends ReqDeleteTypeProjectionGen with ReqEntityProjectionGen {

  override type OpProjectionType = OpEntityProjection
  override type OpTagProjectionEntryType = OpTagProjectionEntry
  override protected type GenType = ReqDeleteEntityProjectionGen

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def normalizedFromGenOpt: Option[ReqDeleteEntityProjectionGen] =
    Option(op.normalizedFrom()).map { nfo =>
      new ReqDeleteEntityProjectionGen(
        baseNamespaceProvider,
        nfo,
        baseNamespaceOpt,
        _namespaceSuffix,
        None,
        ctx
      )
    }

  override protected def tailGenerator(op: OpEntityProjection, normalized: Boolean): ReqDeleteEntityProjectionGen =
    new ReqDeleteEntityProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(this),
      ctx
    ) {
      override lazy val normalizedTailGenerators: Map[OpEntityProjection, ReqProjectionGen] = Map()
    }


  override protected def tagGenerator(
    pgo: Option[ReqEntityProjectionGen],
    tpe: OpTagProjectionEntry): ReqProjectionGen =
    tagGenerator(
      tpe,
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqDeleteModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpTagProjectionEntry,
    parentTagGenOpt: Option[ReqDeleteModelProjectionGen]): ReqProjectionGen =
    ReqDeleteModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.modelProjection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteVarProjection"),
//    Qn.fromDotSeparated("ws.epigraph.projections.req.delete.ReqDeleteTagProjectionEntry")
//  )
}

object ReqDeleteEntityProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpProjection[_, _],
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqDeleteTypeProjectionGen],
    ctx: GenContext): ReqDeleteTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      ctx.reqDeleteProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqDeleteEntityProjectionGen(
            baseNamespaceProvider,
            op.asEntityProjection(),
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteEntityProjectionGen]),
            ctx
          )

        case _ =>
          val modelOp: OpModelProjection[_, _, _ <: DatumTypeApi, _] =
            op.asModelProjection().asInstanceOf[OpModelProjection[_, _, _ <: DatumTypeApi, _]]

          ReqDeleteModelProjectionGen.dataProjectionGen(
            baseNamespaceProvider,
            modelOp,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqDeleteModelProjectionGen]),
            ctx
          )

      }

    )
}
