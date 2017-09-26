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
class ReqUpdateRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpRecordModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqUpdateModelProjectionGen],
  ctx: GenContext)
  extends ReqUpdateModelProjectionGen(
    baseNamespaceProvider,
    op,
    baseNamespaceOpt,
    _namespaceSuffix,
    parentClassGenOpt,
    ctx
  ) with ReqRecordModelProjectionGen {

  override type OpProjectionType = OpRecordModelProjection
  override type OpFieldProjectionType = OpFieldProjectionEntry

  override lazy val fieldGenerators: Map[CField, ReqUpdateFieldProjectionGen] =
    fieldProjections.values.map { case (fgo, fpe) =>
      val field = fpe.field()
      val cField = findField(field.name())

      def fieldGen(parentFieldGenOpt: Option[ReqUpdateTypeProjectionGen]) = {
        new ReqUpdateFieldProjectionGen(
          baseNamespaceProvider,
          field.name(),
          fpe.fieldProjection(),
          Some(baseNamespace),
          namespaceSuffix.append(jn(field.name()).toLowerCase),
          parentFieldGenOpt,
          ctx
        )
      }

      cField ->
      fieldGen(fgo.flatMap(fg => fg.findFieldGenerator(field.name()).map(_.dataProjectionGen.asInstanceOf[ReqUpdateTypeProjectionGen])))

    }.toListMap

  override protected def tailGenerator(
    parentGen: ReqUpdateModelProjectionGen,
    op: OpRecordModelProjection,
    normalized: Boolean) =
    new ReqUpdateRecordModelProjectionGen( // don't use cache here!
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(parentGen),
      ctx
    )
//    {
//      override protected val buildTails: Boolean = !normalized
//      override protected val buildNormalizedTails: Boolean = normalized
//    }

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateRecordModelProjection"),
//    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateFieldProjectionEntry"),
//    replace
//  )
}
