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
import ws.epigraph.java.JavaGenUtils.TraversableOnceToListMapObject.TraversableOnceToListMap
import ws.epigraph.java.service.projections.req._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.{OpFieldProjectionEntry, OpRecordModelProjection}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpRecordModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqPathModelProjectionGen(baseNamespaceProvider, op, namespaceSuffix, ctx) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpRecordModelProjection

  override type OpFieldProjectionType = OpFieldProjectionEntry

  override lazy val fieldGenerators: Map[CField, ReqPathFieldProjectionGen] =
    fieldProjections.values
      .filter { case (fgo, fpe) => !fpe.fieldProjection().projection().isPathEnd }
      .map { case (fgo, fpe) =>
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
      }.toListMap

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqRecordModelPath"),
//    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqFieldPath")
//  )
}
