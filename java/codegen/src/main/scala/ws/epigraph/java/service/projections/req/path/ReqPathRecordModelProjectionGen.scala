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

import ws.epigraph.compiler.CField
import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.projections.req._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.path.{OpRecordModelPath, OpVarPath}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  protected val op: OpRecordModelPath,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqPathModelProjectionGen(baseNamespaceProvider, op, namespaceSuffix, ctx) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpRecordModelPath

  override protected lazy val fieldGenerators: Map[CField, ReqPathFieldProjectionGen] =
    op.fieldProjections().values()
      .filter { fpe =>
        !OpVarPath.isEnd(fpe.fieldProjection().varProjection()) // todo same for maps/lists?
      }
      .map { fpe =>
        (
          findField(fpe.field().name()),
          new ReqPathFieldProjectionGen(
            baseNamespaceProvider,
            fpe.field().name(),
            fpe.fieldProjection(),
            namespaceSuffix.append(jn(fpe.field().name()).toLowerCase),
            ctx
          )
        )
      }.toMap

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqRecordModelPath"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqFieldPath")
  )
}
