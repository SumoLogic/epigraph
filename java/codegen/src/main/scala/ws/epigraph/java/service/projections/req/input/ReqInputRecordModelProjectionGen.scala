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

package ws.epigraph.java.service.projections.req.input

import ws.epigraph.compiler.CField
import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.projections.req._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.input.OpInputRecordModelProjection

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqInputRecordModelProjectionGen(
  operationInfo: OperationInfo,
  protected val op: OpInputRecordModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqInputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpInputRecordModelProjection

  override protected lazy val fieldGenerators: Map[CField, ReqInputFieldProjectionGen] =
    op.fieldProjections().values().map{ fpe =>
      (
        findField(fpe.field().name()),
        new ReqInputFieldProjectionGen(
          operationInfo,
          fpe.field().name(),
          fpe.fieldProjection(),
          namespaceSuffix.append(jn(fpe.field().name()).toLowerCase),
          ctx
        )
      )
    }.toMap

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputRecordModelProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputFieldProjectionEntry"),
    CodeChunk.empty
  )
}
