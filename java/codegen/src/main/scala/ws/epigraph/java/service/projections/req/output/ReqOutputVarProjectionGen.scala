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

import ws.epigraph.compiler.CTag
import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqProjectionGen, ReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind
import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputVarProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  protected val op: OpOutputVarProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqProjectionGen],
  protected val ctx: GenContext) extends ReqOutputProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpOutputVarProjection
  override type OpTagProjectionEntryType = OpOutputTagProjectionEntry

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceName,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceName, _namespaceSuffix)

  override val shortClassName: String = genShortClassName(classNamePrefix, classNameSuffix)

  override protected def tailGenerator(op: OpOutputVarProjection, normalized: Boolean) =
    new ReqOutputVarProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(this),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpOutputVarProjection, ReqProjectionGen] = Map()
    }

  override protected def tagGenerator(tpe: OpOutputTagProjectionEntry): ReqProjectionGen = tagGenerator(tpe, None)

  protected def tagGenerator(
    tpe: OpOutputTagProjectionEntry,
    parentTagGenOpt: Option[ReqProjectionGen]): ReqProjectionGen =
    ReqOutputModelProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      tpe.projection(),
      Some(baseNamespace),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      parentTagGenOpt,
      ctx
    )

  override protected lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    op.tagProjections().values().map { tpe =>
      val tag = tpe.tag()
      val cTag = findTag(tag.name())

      (
        cTag,

        // 3 options here:

        // 1: parent projection exists and tag is inherited -> use parent projection's tag projection
        // 2: parent projection exists and tag is overriden -> create new tag projection extending parent field projection
        // 3: no parent projection -> use simple tag projection

        (parentClassGenOpt match {
          case Some(g: ReqOutputVarProjectionGen) => g.tagGenerators.get(cTag).orElse { // (1)
            g.tagGenerators.find(_._1.name == tag.name()).map(_._2).map { parentTagGen =>
              tagGenerator(tpe, Some(parentTagGen)) // (2)
            }
          }
          case _ => None
        }).getOrElse(tagGenerator(tpe, None)) // (3)

      )
    }.toMap

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputVarProjection"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry")
  )
}

object ReqOutputVarProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpOutputVarProjection,
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    parentClassGenOpt: Option[ReqProjectionGen],
    ctx: GenContext): ReqOutputProjectionGen = op.`type`().kind() match {

    case TypeKind.ENTITY =>
      new ReqOutputVarProjectionGen(
        baseNamespaceProvider,
        op,
        baseNamespaceOpt,
        namespaceSuffix,
        parentClassGenOpt,
        ctx
      )

    case TypeKind.RECORD =>
      new ReqOutputRecordModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        parentClassGenOpt,
        ctx
      )
    case TypeKind.MAP =>
      new ReqOutputMapModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        parentClassGenOpt,
        ctx
      )
    case TypeKind.LIST =>
      new ReqOutputListModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        parentClassGenOpt,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqOutputPrimitiveModelProjectionGen(
        baseNamespaceProvider,
        op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        parentClassGenOpt,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
