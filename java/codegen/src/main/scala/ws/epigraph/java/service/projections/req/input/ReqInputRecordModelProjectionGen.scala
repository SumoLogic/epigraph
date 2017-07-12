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
import ws.epigraph.projections.op.input.{OpInputFieldProjectionEntry, OpInputRecordModelProjection}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqInputRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpInputRecordModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqInputModelProjectionGen(baseNamespaceProvider, op, baseNamespaceOpt, _namespaceSuffix, ctx) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpInputRecordModelProjection
  override type OpFieldProjectionType = OpInputFieldProjectionEntry

  override lazy val fieldGenerators: Map[CField, ReqInputFieldProjectionGen] =
    fieldProjections.values.map { case (fgo, fpe) => // todo
      (
        findField(fpe.field().name()),
        new ReqInputFieldProjectionGen(
          baseNamespaceProvider,
          fpe.field().name(),
          fpe.fieldProjection(),
          Some(baseNamespace),
          namespaceSuffix.append(jn(fpe.field().name()).toLowerCase),
          ctx
        )
      )
    }.toMap

  override protected def tailGenerator(parentGen: ReqInputModelProjectionGen, op: OpInputRecordModelProjection, normalized: Boolean) =
    new ReqInputRecordModelProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      ctx) {
      override protected val buildTails: Boolean = !normalized
      override protected val buildNormalizedTails: Boolean = normalized
      override protected val parentClassGenOpt = Some(parentGen)
    }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputRecordModelProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputFieldProjectionEntry")
  )
}
