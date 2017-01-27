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

import ws.epigraph.compiler.{CListType, CTypeKind}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.req.output.{ReqOutputListModelProjection, ReqOutputMapModelProjection, ReqOutputPrimitiveModelProjection, ReqOutputRecordModelProjection}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputListModelProjectionGen(t: CListType, ctx: GenContext) extends JavaGen[CListType](ctx) {

  protected override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  private def namespace: Qn = Qn.fromDotSeparated(pn(t)).append("projections") // or ".projections.req.output" ?

  private def shortClassName = ReqOutputListModelProjectionGen.shortClassName(ln(t))

  override protected def generate: String = {

    ctx.reqOutputProjections.put(t.name, namespace.append(shortClassName))

    var imports: Set[String] = Set("ws.epigraph.projections.req.output.ReqOutputListModelProjection")

    val elementType = t.elementTypeRef.resolved
    val elementKind = elementType.kind

    val (itemProjectionClass, itemProjectionExpr) =
      if (elementKind.isPrimitive) {
        imports += classOf[ReqOutputPrimitiveModelProjection].getName
        ("ReqOutputPrimitiveModelProjection", "(ReqOutputPrimitiveModelProjection) raw.itemsProjection().pathTagProjection().projection()")
      } else elementKind match {
          case CTypeKind.VARTYPE =>
            val pc = ReqOutputVarProjectionGen.shortClassName(ln(elementType))
            (pc, s"new $pc(raw.itemsProjection())")
          case CTypeKind.RECORD =>
            imports += classOf[ReqOutputRecordModelProjection].getName
            val pc = ReqOutputRecordModelProjectionGen.shortClassName(ln(elementType))
            (pc, s"new $pc((ReqOutputRecordModelProjection) raw.itemsProjection().pathTagProjection().projection())")
          case CTypeKind.MAP =>
            imports += classOf[ReqOutputMapModelProjection].getName
            val pc = ReqOutputMapModelProjectionGen.shortClassName(ln(elementType))
            (pc, s"new $pc((ReqOutputMapModelProjection) raw.itemsProjection().pathTagProjection().projection())")
          case CTypeKind.LIST =>
            imports += classOf[ReqOutputListModelProjection].getName
            val pc = ReqOutputListModelProjectionGen.shortClassName(ln(elementType))
            (pc, s"new $pc((ReqOutputListModelProjection) raw.itemsProjection().pathTagProjection().projection())")
          case _ => throw new RuntimeException("Unexpected model kind: " + elementType)
      }


    val body =
    /*@formatter:off*/sn"""\
/**
 * Request output projection for @{code ${ln(t)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputListModelProjection raw;

  public $shortClassName(@NotNull ReqOutputListModelProjection raw) { this.raw = raw; }

  /**
   * @return items projection
   */
  public @NotNull $itemProjectionClass itemsProjection() {
    return $itemProjectionExpr;
  }

  public @NotNull ReqOutputListModelProjection _raw() { return raw; }
}"""/*@formatter:on*/

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

import org.jetbrains.annotations.NotNull;

${imports.toList.sorted.mkString("import ", ";\nimport ", ";\n")}
$body
"""/*@formatter:on*/
  }


}

object ReqOutputListModelProjectionGen {
  def shortClassName(ln: String) = s"ReqOutput${ln}Projection"
}
