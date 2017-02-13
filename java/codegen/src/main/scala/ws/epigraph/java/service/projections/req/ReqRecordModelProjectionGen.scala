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

import ws.epigraph.compiler.{CField, CRecordTypeDef}
import ws.epigraph.java.JavaGenNames.jn
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.GenRecordModelProjection
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqRecordModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenRecordModelProjection[_, _, _, _, _, _, _ <: DatumTypeApi]

  protected def fieldGenerators: Map[CField, ReqFieldProjectionGen]

  // ------

  protected val cRecordType: CRecordTypeDef = cType.asInstanceOf[CRecordTypeDef]

  override lazy val children: Iterable[ReqProjectionGen] =
    if (ReqFieldProjectionGen.generateFieldProjections)
      fieldGenerators.values
    else
      fieldGenerators.values.flatMap(_.children)

  protected def findField(name: String): CField = cRecordType.effectiveFields.find(_.name == name).getOrElse{
    throw new RuntimeException(s"Can't find field '$name' in type '${cType.name.toString}'")
  }

  protected def generate(reqRecordModelProjectionFqn: Qn, reqFieldProjectionEntryFqn: Qn, extra: CodeChunk): String = {

    def genField(field: CField, fieldGenerator: ReqFieldProjectionGen): CodeChunk = {

      lazy val fieldProjection = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${field.name}} field projection
   */
  public @Nullable ${fieldGenerator.shortClassName} ${jn(field.name)}FieldProjection() {
    ${reqFieldProjectionEntryFqn.last()} fpe = raw.fieldProjection("${field.name}");
    return fpe == null ? null : new ${fieldGenerator.shortClassName}(fpe.fieldProjection());
  }
"""/*@formatter:on*/

      val dataGenerator = fieldGenerator.dataProjectionGen
      val modelProjection = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${field.name}} field data projection
   */
  public @Nullable ${dataGenerator.fullClassName} ${jn(field.name)}() {
    ${reqFieldProjectionEntryFqn.last()} fpe = raw.fieldProjection("${field.name}");
    return fpe == null ? null : new ${dataGenerator.fullClassName}(fpe.fieldProjection().varProjection());
  }
"""/*@formatter:on*/

      if (ReqFieldProjectionGen.generateFieldProjections)
        CodeChunk(
          fieldProjection ++ modelProjection,
          Set(fieldGenerator.fullClassName,
            "org.jetbrains.annotations.Nullable",
            reqFieldProjectionEntryFqn.toString
          )
        )
      else
        CodeChunk(
          modelProjection,
          Set(
            "org.jetbrains.annotations.Nullable",
            reqFieldProjectionEntryFqn.toString
          )
        )
    }

    val fields = fieldGenerators.map{ case (field, gen) => genField(field, gen) }.foldLeft(CodeChunk.empty)(_ + _)

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString,
      reqModelProjectionQn.toString,
      reqRecordModelProjectionFqn.toString
    ) ++ fields.imports ++ params.imports ++ meta.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ${reqRecordModelProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqModelProjectionQn.last()}$reqModelProjectionParams raw) {
    this.raw = (${reqRecordModelProjectionFqn.last()} ) raw;
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${extra.code}\
${fields.code}\
${params.code}\
${meta.code}\

  public @NotNull ${reqRecordModelProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
