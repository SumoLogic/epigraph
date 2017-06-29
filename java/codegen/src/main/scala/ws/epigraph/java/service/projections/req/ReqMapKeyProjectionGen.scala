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

import ws.epigraph.compiler.{CDatumType, CMapType}
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpKeyProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqMapKeyProjectionGen extends ReqProjectionGen {
  type OpKeyProjectionType <: OpKeyProjection

  protected def op: OpKeyProjectionType

  protected def cMapType: CMapType

  // ------

  protected val mapTypeShortName: String = ln(cMapType)

  protected def generate(reqKeyProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {
    val params = ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

    val keyType: CDatumType = cMapType.keyTypeRef.resolved.asInstanceOf[CDatumType]
    val keyTypeShortName = ln(keyType)

    def genPrimitiveKey(nativeType: String): String = /*@formatter:off*/sn"""\
  public @NotNull $nativeType value() {
    $keyTypeShortName key = ($keyTypeShortName) raw.value();
    return key.getVal();
  }
"""/*@formatter:on*/

    def genNonPrimitiveKey: String = /*@formatter:off*/sn"""\
  public @NotNull $keyTypeShortName.Imm value() {
    return (($keyTypeShortName) raw.value()).toImmutable();
  }
"""/*@formatter:on*/

    // unwrap built-in primitives

    val keyCode = CodeChunk(JavaGenUtils.builtInPrimitives.get(keyType.name.name).map(genPrimitiveKey).getOrElse(genNonPrimitiveKey))

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqKeyProjectionFqn.toString,
      lqn2(keyType, namespace.toString)
    ) ++ params.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request projection for {@code $mapTypeShortName} keys
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName {
  private final @NotNull ${reqKeyProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqKeyProjectionFqn.last()} raw) {
    this.raw = raw;
  }

  /**
   * @return key value
   */
${(keyCode + params + extra).code}\

  public @NotNull ${reqKeyProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
