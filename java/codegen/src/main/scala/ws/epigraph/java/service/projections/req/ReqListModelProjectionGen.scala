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

import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.GenListModelProjection
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqListModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenListModelProjection[_, _, _, _, _ <: DatumTypeApi]

  protected val elementsNamespaceSuffix = "elements"

  protected def elementGen: ReqProjectionGen

  override def children: Iterable[JavaGen] = super.children ++ Iterable(elementGen)

  protected def generate(reqListModelProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {
    val elementProjectionClass = elementGen.shortClassName

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString,
      reqModelProjectionFqn.toString,
      reqListModelProjectionFqn.toString,
      elementGen.fullClassName
    ) ++ params.imports ++ meta.imports ++ tails.imports ++ normalizedTails.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName $extendsClause{
${if (parentClassGenOpt.isEmpty) s"  protected final @NotNull ${reqListModelProjectionFqn.last()} raw;\n" else ""}\

  public $shortClassName(@NotNull ${reqModelProjectionFqn.last()}$reqModelProjectionParams raw) {
    ${if (parentClassGenOpt.isEmpty) s"this.raw = (${reqListModelProjectionFqn.last()}) raw" else "super(raw)"};
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

  /**
   * @return items projection
   */
  public @NotNull $elementProjectionClass itemsProjection() {
    return new $elementProjectionClass(raw.itemsProjection());
  }\
\s${(extra + params + meta + tails + normalizedTails).code}\
${if (parentClassGenOpt.isEmpty) s"\n  public @NotNull ${reqListModelProjectionFqn.last()} _raw() { return raw; };\n\n" else ""}\
}"""/*@formatter:on*/
  }
}
