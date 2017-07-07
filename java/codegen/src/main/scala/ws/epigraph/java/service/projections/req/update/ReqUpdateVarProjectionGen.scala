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
import ws.epigraph.projections.op.input._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateVarProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpInputVarProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqUpdateProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpInputVarProjection
  override type OpTagProjectionEntryType = OpInputTagProjectionEntry

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceName,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceName, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpInputVarProjection, normalized: Boolean) =
    new ReqUpdateVarProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx
    ) {
      override lazy val normalizedTailGenerators: Map[OpInputVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(tpe: OpInputTagProjectionEntry): ReqProjectionGen =
    ReqUpdateModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateVarProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateTagProjectionEntry"),

  CodeChunk(/*@formatter:off*/sn"""\
  /**
   * @return {@code true} if entity must be replaced (updated), and {@code false} if it must be patched
   */
  public boolean replace() {
    return raw.replace();
  }
"""/*@formatter:on*/)
  )
}

object ReqUpdateVarProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpInputVarProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    ctx: GenContext): ReqUpdateProjectionGen = op.`type`().kind() match {

    case TypeKind.ENTITY =>
      new ReqUpdateVarProjectionGen(baseNamespaceProvider, op, baseNamespaceOpt, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqUpdateRecordModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpInputRecordModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqUpdateMapModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpInputMapModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqUpdateListModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpInputListModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqUpdatePrimitiveModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpInputPrimitiveModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
