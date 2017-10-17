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
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqEntityProjectionGen, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathEntityProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpEntityProjection,
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqPathTypeProjectionGen with ReqEntityProjectionGen {

  override type OpProjectionType = OpEntityProjection
  override type OpTagProjectionEntryType = OpTagProjectionEntry

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def tailGenerator(
    op: OpEntityProjection,
    normalized: Boolean): ReqProjectionGen = throw new RuntimeException("paths have no tails")

  override protected def tagGenerator(pgo: Option[ReqEntityProjectionGen], tpe: OpTagProjectionEntry): ReqPathProjectionGen =
    ReqPathModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqVarPath"),
//    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqTagPath")
//  )
}

object ReqPathEntityProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpEntityProjection,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqPathTypeProjectionGen = op.`type`().kind() match {

    case TypeKind.ENTITY =>
      new ReqPathEntityProjectionGen(baseNamespaceProvider, op, namespaceSuffix, ctx)


    case _ =>
      val modelOp: OpModelProjection[_, _, _ <: DatumTypeApi, _] =
        op.singleTagProjection().projection().asInstanceOf[OpModelProjection[_, _, _ <: DatumTypeApi, _]]

      ReqPathModelProjectionGen.dataProjectionGen(
        baseNamespaceProvider,
        modelOp,
        namespaceSuffix,
        ctx
      )
  }
}
