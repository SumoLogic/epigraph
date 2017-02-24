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
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqPrimitiveModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenPrimitiveModelProjection[_, _, _ <: DatumTypeApi]

  protected def generate(reqPrimitiveModelProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString,
      reqModelProjectionFqn.toString,
      reqPrimitiveModelProjectionFqn.toString
    ) ++ params.imports ++ meta.imports ++ tails.imports ++ normalizedTails.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ${reqPrimitiveModelProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqModelProjectionFqn.last()}$reqModelProjectionParams raw) {
    this.raw = (${reqPrimitiveModelProjectionFqn.last()}) raw;
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }\
\s${(extra + params + meta + tails + normalizedTails).code}\

  public @NotNull ${reqPrimitiveModelProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
