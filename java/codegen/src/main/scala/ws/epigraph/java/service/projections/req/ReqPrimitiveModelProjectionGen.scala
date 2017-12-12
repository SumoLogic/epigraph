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

import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.GenPrimitiveModelProjection
import ws.epigraph.projections.op.OpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqPrimitiveModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: OpModelProjection[_, _, _ <: DatumTypeApi, _] with GenPrimitiveModelProjection[_, _, _, _, _ <: DatumTypeApi]

  protected def generate(reqPrimitiveModelProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {
    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqProjectionFqn.toString,
      reqPrimitiveModelProjectionFqn.toString
    ) ++ params.imports ++ meta.imports ++ tails.imports ++ normalizedTails.imports ++ dispatcher.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${JavaGenUtils.generateImports(imports)}

$classJavadoc\
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName $extendsClause{
${if (parentClassGenOpt.isEmpty) s"  protected final @NotNull ${reqPrimitiveModelProjectionFqn.last()} raw;\n" else ""}\

  public $shortClassName(@NotNull ${reqProjectionFqn.last()}$reqProjectionParams raw) {
    ${if (parentClassGenOpt.isEmpty) s"this.raw = (${reqPrimitiveModelProjectionFqn.last()}) raw" else "super(raw)"};
  }\
\s${(extra + params + meta + tails + normalizedTails + dispatcher).code}\
${if (parentClassGenOpt.isEmpty) s"\n  public @NotNull ${reqPrimitiveModelProjectionFqn.last()} _raw() { return raw; };\n\n" else ""}\
}"""/*@formatter:on*/
  }

  protected def generate0: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqPrimitiveModelProjection"),
    flag
  )
}
