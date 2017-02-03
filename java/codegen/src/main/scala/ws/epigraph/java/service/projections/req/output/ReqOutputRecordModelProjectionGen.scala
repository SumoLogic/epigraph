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
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{CodeChunk, OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputRecordModelProjection

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputRecordModelProjectionGen(
  operationInfo: OperationInfo,
  protected val op: OpOutputRecordModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  override type OpProjectionType = OpOutputRecordModelProjection

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

    def genField(field: CField, fieldGenerator: ReqOutputFieldProjectionGen): CodeChunk = {

      lazy val fieldProjection = /*@formatter:off*/sn"""\
  /**
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
  /**
   * @return {@code ${field.name}} field data projection
   */
  public @Nullable ${dataGenerator.fullClassName} ${jn(field.name)}() {
    ReqOutputFieldProjectionEntry fpe = raw.fieldProjection("${field.name}");
    return fpe == null ? null : new ${dataGenerator.fullClassName}(fpe.fieldProjection().varProjection());
  }
"""/*@formatter:on*/

      if (ReqOutputFieldProjectionGen.generateFieldProjections)
        CodeChunk(
          fieldProjection ++ modelProjection,
          Set(fieldGenerator.fullClassName,
            "org.jetbrains.annotations.Nullable",
            "ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"
          )
        )
      else
        CodeChunk(
          modelProjection,
          Set(
            "org.jetbrains.annotations.Nullable",
            "ws.epigraph.projections.req.output.ReqOutputFieldProjectionEntry"
          )
        )
    }

    val fields = fieldGenerators.map{ case (field, gen) => genField(field, gen) }.foldLeft(CodeChunk.empty)(_ + _)

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputRecordModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection"
    ) ++ fields.imports ++ params.imports ++ meta.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ReqOutputRecordModelProjection raw;

  public $shortClassName(@NotNull ReqOutputModelProjection<?, ?, ?> raw) {
    this.raw = (ReqOutputRecordModelProjection) raw;
  }

  public $shortClassName(@NotNull ReqOutputVarProjection selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${required.code}
${fields.code}\
${params.code}\
${meta.code}\

  public @NotNull ReqOutputRecordModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
