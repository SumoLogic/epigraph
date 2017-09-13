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

package ws.epigraph.java.service.projections.req

import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.assemblers.EntityAsmGen
import ws.epigraph.java.service.projections.req.ReqProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.{GenContext, JavaGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqEntityProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpOutputVarProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqEntityProjectionGen],
  protected val ctx: GenContext) extends ReqTypeProjectionGen with AbstractReqVarProjectionGen {

  override type OpProjectionType = OpOutputVarProjection
  override type OpTagProjectionEntryType = OpOutputTagProjectionEntry
  override protected type GenType = ReqEntityProjectionGen

  override protected def baseNamespace: Qn = AbstractReqProjectionGen.baseNamespace(
    referenceNameOpt,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = AbstractReqProjectionGen.namespaceSuffix(referenceNameOpt, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpOutputVarProjection, normalized: Boolean) =
    new ReqEntityProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(this),
      ctx
    ) {
      override lazy val normalizedTailGenerators: Map[OpOutputVarProjection, AbstractReqProjectionGen] = Map()
    }

  override protected def tagGenerator(
    pgo: Option[AbstractReqVarProjectionGen],
    tpe: OpOutputTagProjectionEntry): AbstractReqProjectionGen =
    tagGenerator(
      tpe,
      pgo.flatMap(pg => pg.findTagGenerator(tpe.tag().name()).map(_.asInstanceOf[ReqModelProjectionGen]))
    )

  protected def tagGenerator(
    tpe: OpOutputTagProjectionEntry,
    parentTagGenOpt: Option[ReqModelProjectionGen]): AbstractReqProjectionGen =
    ReqModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

  override lazy val children: Iterable[JavaGen] =
    if (tagGenerators.isEmpty /*|| namespace.contains(Namespaces.TAILS_SEGMENT)*/ ) super.children
    else super.children ++ Iterable(new EntityAsmGen(this, ctx))

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqEntityProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqTagProjectionEntry")
  )
}

object ReqEntityProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpOutputVarProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqTypeProjectionGen],
    ctx: GenContext): ReqTypeProjectionGen =

    ReqTypeProjectionGenCache.lookup(
      Option(op.referenceName()),
      parentClassGenOpt.isDefined,
      op.normalizedFrom() != null,
      ctx.reqOutputProjections,

      op.`type`().kind() match {

        case TypeKind.ENTITY =>
          new ReqEntityProjectionGen(
            baseNamespaceProvider,
            op,
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqEntityProjectionGen]),
            ctx
          )

        case TypeKind.RECORD =>
          new ReqRecordModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqModelProjectionGen]),
            ctx
          )
        case TypeKind.MAP =>
          new ReqMapModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqModelProjectionGen]),
            ctx
          )
        case TypeKind.LIST =>
          new ReqListModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqModelProjectionGen]),
            ctx
          )
        case TypeKind.PRIMITIVE =>
          new ReqPrimitiveModelProjectionGen(
            baseNamespaceProvider,
            op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
            baseNamespaceOpt,
            namespaceSuffix,
            parentClassGenOpt.map(pg => pg.asInstanceOf[ReqModelProjectionGen]),
            ctx
          )
        case x => throw new RuntimeException(s"Unknown projection kind: $x")

      }

    )
}
