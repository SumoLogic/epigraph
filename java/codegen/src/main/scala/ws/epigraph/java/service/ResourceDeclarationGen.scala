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

import ws.epigraph.compiler.CompilerException
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java._
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i, sp}
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations._
import ws.epigraph.types.DataTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ResourceDeclarationGen(rd: ResourceDeclaration, baseNamespace: Qn, val ctx: GenContext) extends ObjectGen[ResourceDeclaration](rd) with JavaGen {
  protected val namespace: Qn = ResourceDeclarationGen.resourceDeclarationNamespace(baseNamespace, rd)
  protected val resourceDeclarationClassName: String = ResourceDeclarationGen.resourceDeclarationClassName(rd)

  override protected def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(ResourceDeclarationGen.resourceDeclarationClassName(rd) + ".java")

  override protected def generateObject(ctx: ObjectGenContext): String = {
    val fieldType: DataTypeApi = rd.fieldType()

    import scala.collection.JavaConversions._
    val operationFieldNames: List[String] = rd.operations().map{ od: OperationDeclaration =>

      val operationFieldName = ResourceDeclarationGen.operationDeclarationFieldName(od)
      val operationConstructorName = ResourceDeclarationGen.operationConstructorName(od)

      ctx.addField(s"public static final ${od.getClass.getSimpleName} $operationFieldName = $operationConstructorName();")
      ctx.addMethod(
        /*@formatter:off*/sn"""\
private static ${od.getClass.getSimpleName} $operationConstructorName() {
  return ${sp(2, gen(od, ctx))};
}"""/*@formatter:on*/
      )

      operationFieldName
    }.toList

    // see JavaTypeGen.dataTypeExpr, typeExpression
    /*@formatter:off*/sn"""\
super(
  "${rd.fieldName()}",
  ${ObjectGenUtils.genDataTypeExpr(rd.fieldType(), ctx.gctx)},
  ${i(ObjectGenUtils.genList(operationFieldNames, ctx))},
  ${gen(rd.location(), ctx)}
)"""/*@formatter:on*/

  }

  override def generate: String = {
    val sgctx = new ObjectGenContext(ctx)
    val superCall = generate(sgctx) // do not inline!
    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ObjectGenUtils.genImports(sgctx)}
@javax.annotation.Generated("${getClass.getCanonicalName}")
public final class $resourceDeclarationClassName extends ResourceDeclaration {
  public static final $resourceDeclarationClassName INSTANCE = new $resourceDeclarationClassName();

  ${i(ObjectGenUtils.genFields(sgctx))}

  static {
    ${i(ObjectGenUtils.genStatic(sgctx))}
  }

  private $resourceDeclarationClassName() {
    ${i(superCall)};
  }

  ${i(ObjectGenUtils.genMethods(sgctx))}
}
"""/*@formatter:on*/

  }

}

object ResourceDeclarationGen {

  def resourceDeclarationNamespace(baseNamespace: Qn, rd: ResourceDeclaration): Qn =
    ServiceNames.resourceNamespace(baseNamespace, rd.fieldName())

  def resourceDeclarationClassName(rd: ResourceDeclaration): String = up(rd.fieldName()) + "ResourceDeclaration"

  def operationDeclarationFieldName(od: OperationDeclaration): String = {
    val kind = ServiceNames.operationKinds.getOrElse(od.kind(), {throw new CompilerException})

    if (od.name() != null)
      s"${od.name()}${up(kind)}OperationDeclaration"
    else
      kind + "OperationDeclaration"
  }

  def operationConstructorName(od: OperationDeclaration): String = {
    val kind = up(ServiceNames.operationKinds.getOrElse(od.kind(), {throw new CompilerException}))

    if (od.name() != null)
      s"construct${up(od.name())}${kind}OperationDeclaration"
    else
      s"construct${kind}OperationDeclaration"
  }

}
