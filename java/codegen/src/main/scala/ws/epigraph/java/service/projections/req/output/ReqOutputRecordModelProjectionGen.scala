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

import ws.epigraph.compiler.{CField, CRecordTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, ln}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputRecordModelProjection

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputRecordModelProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputRecordModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  private val cRecordType = cType.asInstanceOf[CRecordTypeDef]

  private lazy val fieldGenerators: Map[CField, ReqOutputFieldProjectionGen] =
    op.fieldProjections().values().map{ fpe =>
      (
        findField(fpe.field().name()),
        new ReqOutputFieldProjectionGen(
          operationInfo,
          fpe.field().name(),
          fpe.fieldProjection(),
          namespaceSuffix.append(jn(fpe.field().name()).toLowerCase),
          ctx
        )
      )
    }.toMap

  override lazy val children: Iterable[ReqProjectionGen] =
    if (ReqOutputFieldProjectionGen.generateFieldProjections)
      fieldGenerators.values
    else
      fieldGenerators.values.flatMap(_.children)

  private def findField(name: String): CField = cRecordType.effectiveFields.find(_.name == name).getOrElse{
    throw new RuntimeException(s"Can't find field '$name' in type '${cType.name.toString}'")
  }

  override protected def generate: String = {

    def genField(field: CField, fieldGenerator: ReqOutputFieldProjectionGen): (String, Set[String]) = {

      lazy val fieldProjection = /*@formatter:off*/sn"""\
  ${"/**"}
   * @return {@code ${field.name}} field projection
   */
   public @Nullable ${fieldGenerator.shortClassName} ${jn(field.name)}FieldProjection() {
     ReqOutputFieldProjectionEntry fpe = raw.fieldProjection("${field.name}");
     return fpe == null ? null : new ${fieldGenerator.shortClassName}(fpe.fieldProjection());
   }
"""/*@formatter:on*/

      lazy val fieldProjectionImports = Set(fieldGenerator.fullClassName)

      val dataGenerator = fieldGenerator.dataProjectionGen
      val modelProjection = /*@formatter:off*/sn"""\
  ${"/**"}
   * @return {@code ${field.name}} model projection
   */
   public @Nullable ${dataGenerator.fullClassName} ${jn(field.name)}() {
     ReqOutputFieldProjectionEntry fpe = raw.fieldProjection("${field.name}");
     return fpe == null ? null : new ${dataGenerator.fullClassName}(fpe.fieldProjection().varProjection());
   }
"""/*@formatter:on*/

      if (ReqOutputFieldProjectionGen.generateFieldProjections)
        (
          fieldProjection ++ modelProjection,
          Set(fieldGenerator.fullClassName,
            "org.jetbrains.annotations.Nullable",
            "ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"
          )
        )
      else
        (
          modelProjection,
          Set(
            "org.jetbrains.annotations.Nullable",
            "ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"
          )
        )
    }

    val (fieldsCode: String, fieldsImports: Set[String]) =
      fieldGenerators.foldLeft(("", Set[String]())){ case ((code, _imports), (field, gen)) =>
        val (fieldCode, fieldImports) = genField(field, gen)
        val newCode = if (code.isEmpty) "\n" + fieldCode else code + "\n" + fieldCode
        (newCode, _imports ++ fieldImports)
      }

    val (params, paramImports) =
      ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputRecordModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection"
    ) ++ fieldsImports ++ paramImports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputRecordModelProjection raw;

  public $shortClassName(@NotNull ReqOutputModelProjection<?, ?, ?> raw) {
    this.raw = (ReqOutputRecordModelProjection) raw;
  }

  public $shortClassName(@NotNull ReqOutputVarProjection selfVar) {
    this(selfVar.singleTagProjection().projection());
  }
$fieldsCode\
$params\

  public @NotNull ReqOutputRecordModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
