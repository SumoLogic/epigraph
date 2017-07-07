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
import ws.epigraph.projections.op.AbstractOpFieldProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqFieldProjectionGen extends ReqProjectionGen {
  type OpFieldProjectionType <: AbstractOpFieldProjection[_, _, _, _]

  protected def op: OpFieldProjectionType

  def dataProjectionGen: ReqProjectionGen

  // -----

  override lazy val children: Iterable[ReqProjectionGen] = Iterable(dataProjectionGen)

  protected def generate(fieldName: String, reqFieldProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {
//    val params =
//      ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

    val imports: Set[String] = /*params.imports ++*/ Set(
      "org.jetbrains.annotations.NotNull",
      reqFieldProjectionFqn.toString,
      dataProjectionGen.fullClassName
    ) ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${JavaGenUtils.generateImports(imports)}

/**
 * Request projection for {@code $fieldName} field
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName {
  private final @NotNull ${reqFieldProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqFieldProjectionFqn.last()} raw) { this.raw = raw; }

  /**
   * @return field data projection
   */
  public @NotNull ${dataProjectionGen.shortClassName} dataProjection() {
    return new ${dataProjectionGen.shortClassName}(raw.varProjection());
  }
${/*params.code*/""}\
${extra.code}\

  public @NotNull ${reqFieldProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}

object ReqFieldProjectionGen {
  val generateFieldProjections = false // take from settings?
}
