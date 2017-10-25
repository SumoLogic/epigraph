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

import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.assemblers.EntityAsmGen
import ws.epigraph.java.service.projections.req._
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.{GenContext, JavaGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputEntityProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpEntityProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqOutputEntityProjectionGen],
  protected val ctx: GenContext) extends ReqOutputTypeProjectionGen with ReqEntityProjectionGen {

  override type OpProjectionType = OpEntityProjection
  override type OpTagProjectionEntryType = OpTagProjectionEntry
  override protected type GenType = ReqOutputEntityProjectionGen

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def normalizedFromGenOpt: Option[ReqOutputEntityProjectionGen] =
    Option(op.normalizedFrom()).map { nfo =>
      new ReqOutputEntityProjectionGen(
        baseNamespaceProvider,
        nfo,
        baseNamespaceOpt,
        _namespaceSuffix,
        None,
        ctx
      )
    }

  override protected def tailGenerator(op: OpEntityProjection, normalized: Boolean): ReqOutputEntityProjectionGen =
    new ReqOutputEntityProjectionGen(
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
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqOutputModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpTagProjectionEntry,
    parentTagGenOpt: Option[ReqOutputModelProjectionGen]): ReqProjectionGen =
    ReqOutputModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

  override lazy val children: Iterable[JavaGen] =
    if (tagGenerators.isEmpty /*|| namespace.contains(Namespaces.TAILS_SEGMENT)*/ ) super.children
    else super.children ++ Iterable(new EntityAsmGen(this, ctx))

  override protected lazy val flag: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  /**
   * @return {@code true} if entity is requried
   */
  public boolean required() {
    return raw.flag();
  }
"""/*@formatter:on*/
  )

}

object ReqOutputEntityProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpEntityProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqOutputTypeProjectionGen],
    ctx: GenContext): ReqOutputTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      ctx.reqOutputProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqOutputEntityProjectionGen(
            baseNamespaceProvider,
            op,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqOutputEntityProjectionGen]),
            ctx
          )

        case _ =>
          val modelOp: OpModelProjection[_, _, _ <: DatumTypeApi, _] =
            op.singleTagProjection().projection().asInstanceOf[OpModelProjection[_, _, _ <: DatumTypeApi, _]]

          ReqOutputModelProjectionGen.dataProjectionGen(
            baseNamespaceProvider,
            modelOp,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqOutputModelProjectionGen]),
            ctx
          )

      }

    )
}
