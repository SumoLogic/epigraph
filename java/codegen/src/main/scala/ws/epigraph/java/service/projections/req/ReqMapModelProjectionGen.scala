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

import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.GenMapModelProjection
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqMapModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenMapModelProjection[_, _, _, _, _ <: DatumTypeApi]

  protected val elementsNamespaceSuffix = "elements"

  def keyGen: ReqMapKeyProjectionGen

  def elementGen: ReqTypeProjectionGen

  protected def keysNullable: Boolean = true

  // -------

  override def children: Iterable[JavaGen] = super.children ++ Iterable(keyGen, elementGen)

  protected def keys: CodeChunk = {
    val keyProjectionClass = keyGen.shortClassName

    if (keysNullable) {
      CodeChunk(
        /*@formatter:off*/sn"""\
  /**
   * @return key projections
   */
  public @Nullable List<$keyProjectionClass> keys() {
    return raw.keys() == null ? null : raw.keys().stream().map(key -> new $keyProjectionClass(key)).collect(Collectors.toList());
  }
"""/*@formatter:on*/
      )
    } else {
      CodeChunk(
        /*@formatter:off*/sn"""\
  /**
   * @return key projections
   */
  public @NotNull List<$keyProjectionClass> keys() {
    assert raw.keys() != null;
    return raw.keys().stream().map(key -> new $keyProjectionClass(key)).collect(Collectors.toList());
  }
"""/*@formatter:on*/
      )
    }
  }

  protected def generate(reqMapModelProjectionFqn: Qn, extra: CodeChunk = CodeChunk.empty): String = {
    val elementProjectionClass = elementGen.shortClassName
    val _keys = keys

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.List",
      "java.util.stream.Collectors",
      reqVarProjectionFqn.toString,
      reqModelProjectionFqn.toString,
      reqMapModelProjectionFqn.toString,
      elementGen.fullClassName,
      keyGen.fullClassName
    ) ++ params.imports ++ meta.imports ++ extra.imports ++ _keys.imports ++ tails.imports ++ normalizedTails.imports ++ dispatcher.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${JavaGenUtils.generateImports(imports)}

$classJavadoc\
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName $extendsClause{
${if (parentClassGenOpt.isEmpty) s"  protected final @NotNull ${reqMapModelProjectionFqn.last()} raw;\n" else ""}\

  public $shortClassName(@NotNull ${reqModelProjectionFqn.last()}$reqModelProjectionParams raw) {
    ${if (parentClassGenOpt.isEmpty) s"this.raw = (${reqMapModelProjectionFqn.last()}) raw" else "super(raw)"};
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${keys.code}\

  /**
   * @return items projection
   */
  public @NotNull $elementProjectionClass itemsProjection() {
    return new $elementProjectionClass(raw.itemsProjection());
  }\
\s${(extra + params + meta + tails + normalizedTails + dispatcher).code}\
${if (parentClassGenOpt.isEmpty) s"\n  public @NotNull ${reqMapModelProjectionFqn.last()} _raw() { return raw; };\n\n" else ""}\
}"""/*@formatter:on*/
  }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.ReqMapModelProjection"),
    flagged
  )
}
