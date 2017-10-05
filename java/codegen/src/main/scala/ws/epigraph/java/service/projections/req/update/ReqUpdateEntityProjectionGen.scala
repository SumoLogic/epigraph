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

package ws.epigraph.java.service.projections.req.update

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req._
import ws.epigraph.java.service.projections.req.update.ReqUpdateProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateEntityProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpEntityProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqUpdateEntityProjectionGen],
  protected val ctx: GenContext) extends ReqUpdateTypeProjectionGen with ReqEntityProjectionGen {

  override type OpProjectionType = OpEntityProjection
  override type OpTagProjectionEntryType = OpTagProjectionEntry
  override protected type GenType = ReqUpdateEntityProjectionGen

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpEntityProjection, normalized: Boolean): ReqUpdateEntityProjectionGen =
    new ReqUpdateEntityProjectionGen(
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
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqUpdateModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpTagProjectionEntry,
    parentTagGenOpt: Option[ReqUpdateModelProjectionGen]): ReqProjectionGen =
    ReqUpdateModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

  override protected lazy val flag: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  /**
   * @return {@code true} if entity must be replaced (updated), and {@code false} if it must be patched
   */
  public boolean replace() {
    return raw.flag();
  }
"""/*@formatter:on*/
  )
}

object ReqUpdateEntityProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpEntityProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqUpdateTypeProjectionGen],
    ctx: GenContext): ReqUpdateTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqUpdateProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqUpdateEntityProjectionGen(
            baseNamespaceProvider,
            op,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqUpdateEntityProjectionGen]),
            ctx
          )

        case TypeKind.RECORD =>
          new ReqUpdateRecordModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqUpdateModelProjectionGen]),
            ctx
          )
        case TypeKind.MAP =>
          new ReqUpdateMapModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqUpdateModelProjectionGen]),
            ctx
          )
        case TypeKind.LIST =>
          new ReqUpdateListModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqUpdateModelProjectionGen]),
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqUpdatePrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpPrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqUpdateModelProjectionGen]),
            ctx
          )
        case x => throw new RuntimeException(s"Unknown projection kind: $x")

      }

    )
}
