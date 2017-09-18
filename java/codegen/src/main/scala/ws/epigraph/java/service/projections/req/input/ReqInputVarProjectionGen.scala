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

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.projections.req.input.ReqInputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqProjectionGen, ReqTypeProjectionGenCache, ReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqInputVarProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpOutputVarProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqInputVarProjectionGen],
  protected val ctx: GenContext) extends ReqInputTypeProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpOutputVarProjection
  override type OpTagProjectionEntryType = OpOutputTagProjectionEntry
  override protected type GenType = ReqInputVarProjectionGen

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpOutputVarProjection, normalized: Boolean): ReqInputVarProjectionGen =
    new ReqInputVarProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(this),
      ctx
    ) {
      override lazy val normalizedTailGenerators: Map[OpOutputVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(
    pgo: Option[ReqVarProjectionGen],
    tpe: OpOutputTagProjectionEntry): ReqProjectionGen =
    tagGenerator(
      tpe,
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqInputModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpOutputTagProjectionEntry,
    parentTagGenOpt: Option[ReqInputModelProjectionGen]): ReqProjectionGen =
    ReqInputModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputVarProjection"),
//    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputTagProjectionEntry")
//  )
}

object ReqInputVarProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpOutputVarProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqInputTypeProjectionGen],
    ctx: GenContext): ReqInputTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqInputProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqInputVarProjectionGen(
            baseNamespaceProvider,
            op,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqInputVarProjectionGen]),
            ctx
          )
        case TypeKind.RECORD =>
          new ReqInputRecordModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqInputModelProjectionGen]),
            ctx
          )
        case TypeKind.MAP =>
          new ReqInputMapModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqInputModelProjectionGen]),
            ctx
          )
        case TypeKind.LIST =>
          new ReqInputListModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqInputModelProjectionGen]),
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqInputPrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqInputModelProjectionGen]),
            ctx
          )
        case x => throw new RuntimeException(s"Unknown projection kind: $x")

      }
    )
}
