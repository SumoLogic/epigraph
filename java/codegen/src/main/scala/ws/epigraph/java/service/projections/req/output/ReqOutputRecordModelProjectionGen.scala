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

import ws.epigraph.compiler.CField
import ws.epigraph.java.{GenContext, JavaGen}
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.builders.RecordBuilderGen
import ws.epigraph.java.service.projections.req._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputRecordModelProjection

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpOutputRecordModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqProjectionGen],
  ctx: GenContext)
  extends ReqOutputModelProjectionGen(
    baseNamespaceProvider,
    op,
    baseNamespaceOpt,
    _namespaceSuffix,
    parentClassGenOpt,
    ctx
  ) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpOutputRecordModelProjection

  override lazy val fieldGenerators: Map[CField, ReqOutputFieldProjectionGen] =
    op.fieldProjections().values().map { fpe =>
      val field = fpe.field()
      val cField = findField(field.name())

      def fieldGen(parentFieldGenOpt: Option[ReqProjectionGen]) =
        new ReqOutputFieldProjectionGen(
          baseNamespaceProvider,
          field.name(),
          fpe.fieldProjection(),
          Some(baseNamespace),
          namespaceSuffix.append(jn(field.name()).toLowerCase),
          parentFieldGenOpt,
          ctx
        )

      (
        cField,

        // 3 options here:

        // 1: parent projection exists and field is inherited -> use parent projection's field projection
        // 2: parent projection exists and field is overriden -> create new field projection extending parent field projection
        // 3: no parent projection -> use simple field projection

        (parentClassGenOpt match {
          case Some(g: ReqOutputRecordModelProjectionGen) => g.fieldGenerators.get(cField).orElse { // (1)
            g.fieldGenerators.find(_._1.name == field.name()).map(_._2).map { parentFieldGen =>
              fieldGen(Some(parentFieldGen.dataProjectionGen)) // (2)
            }
          }
          case _ => None
        }).getOrElse(fieldGen(None)) // (3)

      )
    }.toMap

  override protected def tailGenerator(
    parentGen: ReqModelProjectionGen,
    op: OpOutputRecordModelProjection,
    normalized: Boolean) =
    new ReqOutputRecordModelProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(parentGen),
      ctx
    ) {
      override protected val buildTails: Boolean = !normalized
      override protected val buildNormalizedTails: Boolean = normalized
    }

  override lazy val children: Iterable[JavaGen] =
    if (fieldGenerators.isEmpty) super.children
    else super.children ++ Iterable(new RecordBuilderGen(this, ctx))

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputRecordModelProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"),
    required
  )
}
