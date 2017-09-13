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

package ws.epigraph.java.service.projections.req.path

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.{jn, ln}
import ws.epigraph.java.service.projections.req.path.ReqPathProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, AbstractReqProjectionGen, AbstractReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.path._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathVarProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpVarPath,
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqPathTypeProjectionGen with AbstractReqVarProjectionGen {

  override type OpProjectionType = OpVarPath
  override type OpTagProjectionEntryType = OpTagPath

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def tailGenerator(
    op: OpVarPath,
    normalized: Boolean): AbstractReqProjectionGen = throw new RuntimeException("paths have no tails")

  override protected def tagGenerator(pgo: Option[AbstractReqVarProjectionGen], tpe: OpTagPath): ReqPathProjectionGen =
    ReqPathModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqVarPath"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqTagPath")
  )
}

object ReqPathVarProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpVarPath,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqPathTypeProjectionGen = op.`type`().kind() match {

    case TypeKind.ENTITY =>
      new ReqPathVarProjectionGen(baseNamespaceProvider, op, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqPathRecordModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpRecordModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqPathMapModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpMapModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqPathPrimitiveModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpPrimitiveModelPath],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown path kind: $x")

  }
}
