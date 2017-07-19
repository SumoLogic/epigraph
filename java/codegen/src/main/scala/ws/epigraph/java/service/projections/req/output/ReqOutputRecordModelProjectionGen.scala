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
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.assemblers.RecordAssemblerGen
import ws.epigraph.java.service.projections.req._
import ws.epigraph.java.{GenContext, JavaGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.{OpOutputFieldProjectionEntry, OpOutputRecordModelProjection}
import ws.epigraph.java.JavaGenUtils.TraversableOnceToListMapObject.TraversableOnceToListMap

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputRecordModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpOutputRecordModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqOutputModelProjectionGen],
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

  override type OpFieldProjectionType = OpOutputFieldProjectionEntry

  override lazy val fieldGenerators: Map[CField, ReqOutputFieldProjectionGen] =
    fieldProjections.values.map { case (fgo, fpe) =>
      val field = fpe.field()
      val cField = findField(field.name())

      def fieldGen(parentFieldGenOpt: Option[ReqOutputTypeProjectionGen]) = {
        //System.out.println(s"$fullClassName :: ${ field.name() } extends ${ parentFieldGenOpt.map(_.fullClassName) } ~ ${fgo.map(_.fullClassName)}")
        new ReqOutputFieldProjectionGen(
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
      fieldGen(fgo.flatMap(fg => fg.findFieldGenerator(field.name()).map(_.dataProjectionGen.asInstanceOf[ReqOutputTypeProjectionGen])))

    }.toListMap

  override protected def tailGenerator(
    parentGen: ReqOutputModelProjectionGen,
    op: OpOutputRecordModelProjection,
    normalized: Boolean) =
    new ReqOutputRecordModelProjectionGen( // don't use cache here!
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

  lazy val assemblerGen = new RecordAssemblerGen(this, ctx)

  override lazy val children: Iterable[JavaGen] =
    if (fieldGenerators.isEmpty /*|| namespace.contains(Namespaces.TAILS_SEGMENT)*/ ) super.children
    else {
      super.children ++ Iterable(assemblerGen)
    }


  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputRecordModelProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"),
    required
  )
}
