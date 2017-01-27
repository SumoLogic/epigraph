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

package ws.epigraph.java.projections.req.output

import java.nio.file.Path

import ws.epigraph.compiler.{CTypeKind, CVarTypeDef}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputVarProjectionGen(t: CVarTypeDef, ctx: GenContext) extends JavaGen[CVarTypeDef](ctx) {

  protected override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  private def namespace: Qn = Qn.fromDotSeparated(pn(t)).append("projections") // or ".projections.req.output" ?

  private def shortClassName = s"ReqOutput${ln(t)}Projection"

  override protected def generate: String = {

    ctx.reqOutputProjections.put(t.name, namespace.append(shortClassName))

    var imports:Set[String] = Set("ws.epigraph.projections.req.output.ReqOutputVarProjection")

    val body =
    /*@formatter:off*/sn"""\
/**
 * Request output projection for ${ln(t)}
 */
public class $shortClassName {
  private final @NotNull ReqOutputVarProjection raw;

  public $shortClassName(@NotNull ReqOutputVarProjection raw) { this.raw = raw; }

${t.effectiveTags.map { tag =>
  imports += "ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry"

  val modelType = tag.typeRef.resolved
  val modelKind = modelType.kind
  if (modelKind.isPrimitive) {
    imports += "ws.epigraph.projections.req.output.ReqOutputPrimitiveModelProjection"
sn"""\
  ${"/**"}
   * @return ${tag.name} projection
   */
  public @Nullable ReqOutputPrimitiveModelProjection ${jn(tag.name)}() {
    ReqOutputTagProjectionEntry tpe = raw.tagProjections().get(${ttr(t, tag.name, namespace.toString)}.name());
    return tpe == null ? null : (ReqOutputPrimitiveModelProjection) tpe.projection();
  }

"""
  } else {
    val (tcn:String, rcn:String) = modelKind match {
      case CTypeKind.RECORD =>
        imports += "ws.epigraph.projections.req.output.ReqOutputRecordModelProjection"
        (ReqOutputRecordModelProjectionGen.shortClassName(ln(modelType)), "ReqOutputRecordModelProjection")
      case CTypeKind.MAP =>
        imports += "ws.epigraph.projections.req.output.ReqOutputMapModelProjection"
        (ReqOutputMapModelProjectionGen.shortClassName(ln(modelType)), "ReqOutputMapModelProjection")
      case CTypeKind.LIST =>
        imports += "ws.epigraph.projections.req.output.ReqOutputListModelProjection"
        (ReqOutputListModelProjectionGen.shortClassName(ln(modelType)), "ReqOutputListModelProjection")
      case _ => throw new RuntimeException("Unexpected model kind: " + modelKind)
    }
sn"""\
  ${"/**"}
   * @return ${tag.name} projection
   */
  public @Nullable $tcn ${jn(tag.name)}() {
    ReqOutputTagProjectionEntry tpe = raw.tagProjections().get(${ttr(t, tag.name, namespace.toString)}.name());
    return tpe == null ? null : new $tcn( ($rcn) tpe.projection() );
  }

"""
  }}.mkString
}\
  /**
   * @return raw projection
   */
  public @NotNull ReqOutputVarProjection _raw() { return raw; }
}"""/*@formatter:on*/

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

${imports.toList.sorted.mkString("import ", ";\nimport ", ";\n")}
$body
"""/*@formatter:on*/
  }

}
