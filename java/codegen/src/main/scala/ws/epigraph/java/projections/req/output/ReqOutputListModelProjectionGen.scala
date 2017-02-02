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

import ws.epigraph.compiler.CListType
import ws.epigraph.java.JavaGenNames.{ln, pn}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputListModelProjectionGen(t: CListType, ctx: GenContext) extends JavaGen(ctx) {

  protected override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  private def namespace: Qn = Qn.fromDotSeparated(pn(t)).append("projections") // or ".projections.req.output" ?

  private def shortClassName = ReqOutputListModelProjectionGen.shortClassName(t)

  override protected def generate: String = {

    ctx.reqOutputProjections.put(t.name, namespace.append(shortClassName))

    val elementType = t.elementTypeRef.resolved
    val pe = ReqOutputProjectionGenUtil.projectionExpr(elementType, ln(elementType))
    val imports: Set[String] = Set("ws.epigraph.projections.req.output.ReqOutputListModelProjection") ++ pe.extraImports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

import org.jetbrains.annotations.NotNull;

${imports.toList.sorted.mkString("import ", ";\nimport ", ";\n")}
/**
 * Request output projection for @{code ${ln(t)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputListModelProjection raw;

  public $shortClassName(@NotNull ReqOutputListModelProjection raw) { this.raw = raw; }

  /**
   * @return items projection
   */
  public @NotNull ${pe.resultType} itemsProjection() {
    return ${pe.fromVarExprBuilder("raw.itemsProjection()")};
  }

  public @NotNull ReqOutputListModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }


}

object ReqOutputListModelProjectionGen {
  def shortClassName(t: CListType) = s"ReqOutput${ln(t)}Projection"
}