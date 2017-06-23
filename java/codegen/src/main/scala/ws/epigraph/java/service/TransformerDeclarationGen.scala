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

package ws.epigraph.java.service

import java.nio.file.Path

import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java._
import ws.epigraph.lang.Qn
import ws.epigraph.schema.TransformerDeclaration
import ws.epigraph.types.TypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class TransformerDeclarationGen(td: TransformerDeclaration, baseNamespace: Qn, val ctx: GenContext) extends ObjectGen[TransformerDeclaration](td) with JavaGen {
  protected val namespace: Qn = TransformerDeclarationGen.transformerDeclarationNamespace(baseNamespace, td)
  protected val transformerDeclarationClassName: String = TransformerDeclarationGen.transformerDeclarationClassName(td)

  override protected def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(TransformerDeclarationGen.transformerDeclarationClassName(td) + ".java")

  override protected def generateObject(ctx: ObjectGenContext): String = {
    val transformerType: TypeApi = td.`type`()

    // see JavaTypeGen.dataTypeExpr, typeExpression
    /*@formatter:off*/sn"""\
super(
  "${td.name()}",
  ${ObjectGenUtils.genTypeExpr(transformerType, ctx.gctx)},
  ${i(gen(td.annotations(), ctx))},
  ${i(gen(td.inputProjection(), ctx))},
  ${i(gen(td.outputProjection(), ctx))},
  ${gen(td.location(), ctx)}
)"""/*@formatter:on*/

  }

  override def generate: String = {
    val sgctx = new ObjectGenContext(ctx, namespace)
    val superCall = generate(sgctx) // do not inline!
    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ObjectGenUtils.genImports(sgctx)}
@javax.annotation.Generated("${getClass.getCanonicalName}")
public final class $transformerDeclarationClassName extends TransformerDeclaration {
  public static final $transformerDeclarationClassName INSTANCE = new $transformerDeclarationClassName();

  private $transformerDeclarationClassName() {
    ${i(superCall)};
  }

}
"""/*@formatter:on*/

  }

}

object TransformerDeclarationGen {

  def transformerDeclarationNamespace(baseNamespace: Qn, td: TransformerDeclaration): Qn =
    ServiceNames.transformerNamespace(baseNamespace, td.name())

  def transformerDeclarationClassName(td: TransformerDeclaration): String = up(td.name()) + "TransformerDeclaration"

}


