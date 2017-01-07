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

import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.types.DataTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ResourceDeclarationGen(rd: ResourceDeclaration) extends ServiceObjectGen[ResourceDeclaration](rd) {

  val serviceClassName: String = JavaGenUtils.up(rd.fieldName()) + "ResourceDeclaration"

  override protected def generateObject(ctx: ServiceGenContext): String = {
    val fieldType: DataTypeApi = rd.fieldType()

    // see JavaTypeGen.dataTypeExpr, typeExpression
    /*@formatter:off*/sn"""\
super(
  "${rd.fieldName()}",
  ${ServiceGenUtils.genDataTypeExpr(rd.fieldType(), ctx.gctx)},
  null,
  ${gen(rd.location(), ctx)}
)"""/*@formatter:on*/

  }

  def generateFile(namespace: String, gctx: GenContext): String = {
    val sgctx = new ServiceGenContext(gctx)

    val superCall = generate(sgctx)

    /*@formatter:off*/sn"""\
package $namespace;

${ServiceGenUtils.genImports(sgctx)}
final class $serviceClassName extends ResourceDeclaration {
  public static final $serviceClassName INSTANCE = new $serviceClassName();

  private $serviceClassName() {
    ${i(superCall)};
  }
}
"""/*@formatter:on*/

  }

  def writeUnder(sourcesRoot: Path, namespace: Qn, gctx: GenContext): Unit = {
    val relativePath = JavaGenUtils.fqnToPath(namespace).resolve(serviceClassName + ".java")
    val contents = generateFile(namespace.toString, gctx)
    JavaGenUtils.writeFile(sourcesRoot, relativePath, contents)
  }
}
