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

import ws.epigraph.compiler.{CTag, CVarTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, ln, ttr}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputVarProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputVarProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputProjectionGen(operationInfo, namespaceSuffix, ctx) {

  // todo we have to deal with poly tails / normalization in generated classes

  private val cType: CVarTypeDef = ReqProjectionGen.toCType(op.`type`()).asInstanceOf[CVarTypeDef]

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  private lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    op.tagProjections().values().map{ tpe =>
      (
        findTag(tpe.tag().name()),
        ReqOutputModelProjectionGen.dataProjectionGen(
          operationInfo,
          tpe.projection(),
          namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase), // todo extract?
          ctx
        )
      )
    }.toMap

  override lazy val children: Iterable[ReqProjectionGen] = tagGenerators.values

  private def findTag(name: String): CTag = cType.effectiveTags.find(_.name == name).getOrElse{
    throw new RuntimeException(s"Can't find tag '$name' in type '${cType.name.toString}'")
  }

  override protected def generate: String = {

    def genTag(tag: CTag, tagGenerator: ReqProjectionGen): (String, Set[String]) = (
      /*@formatter:off*/sn"""\
  ${"/**"}
   * @return ${tag.name} projection
   */
   public @Nullable ${tagGenerator.shortClassName} ${jn(tag.name)}() {
     ReqOutputTagProjectionEntry tpe = raw.tagProjections().get(${ttr(cType, tag.name, namespace.toString)}.name());
     return tpe == null ? null : new ${tagGenerator.shortClassName}(tpe.projection());
   }
"""/*@formatter:on*/ ,
      Set(
        tagGenerator.fullClassName,
        "org.jetbrains.annotations.Nullable",
        "ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry"
      )
    )

    val (tagsCode: String, tagsImports: Set[String]) =
      tagGenerators.foldLeft(("", Set[String]())){ case ((code, _imports), (tag, gen)) =>
        val (tagCode, tagImports) = genTag(tag, gen)
        val newCode = if (code.isEmpty) "\n" + tagCode else code + "\n" + tagCode
        (newCode, _imports ++ tagImports)
      }

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection"
    ) ++ tagsImports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputVarProjection raw;

  public $shortClassName(@NotNull ReqOutputVarProjection raw) { this.raw = raw; }
$tagsCode\

  public @NotNull ReqOutputVarProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}

object ReqOutputVarProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpOutputVarProjection,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqOutputProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqOutputVarProjectionGen(operationInfo, op, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqOutputRecordModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqOutputMapModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqOutputListModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqOutputPrimitiveModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
