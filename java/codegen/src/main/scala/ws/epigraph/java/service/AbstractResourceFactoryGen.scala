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
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i, sp}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations._
import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractResourceFactoryGen(rd: ResourceDeclaration, baseNamespace: Qn, val ctx: GenContext) extends JavaGen {
  protected val namespace: Qn = AbstractResourceFactoryGen.abstractResourceFactoryNamespace(baseNamespace, rd)

  override protected def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(AbstractResourceFactoryGen.abstractResourceFactoryClassName(rd) + ".java")

  private val abstractOperationGens: Map[OperationDeclaration, AbstractOperationGen] =
    rd.operations().map{
      case o: ReadOperationDeclaration => (
        o,
        new AbstractReadOperationGen(
          baseNamespace,
          rd,
          o,
          ctx
        )
      )

      case o: CreateOperationDeclaration => (o, null)
      case o: UpdateOperationDeclaration => (o, null)
      case o: DeleteOperationDeclaration => (o, null)
      case o: CustomOperationDeclaration => (o, null)
    }.toMap


  override def children: Iterable[JavaGen] = super.children ++ abstractOperationGens.values.filter(g => g != null) // todo remove filter

  override def generate: String = {
    val sgctx = new ServiceGenContext(ctx)
    val className = AbstractResourceFactoryGen.abstractResourceFactoryClassName(rd)

    sgctx.addImport("org.jetbrains.annotations.NotNull")
    sgctx.addImport("ws.epigraph.service.Resource")
    sgctx.addImport("ws.epigraph.service.ServiceInitializationException")

    val resourceConstructor = generateResourceConstructor(sgctx)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ServiceGenUtils.genImports(sgctx)}
/**
 * Abstract factory for constructing `${rd.fieldName()}` resource implementation
 */
public abstract class $className {
  ${i(ServiceGenUtils.genFields(sgctx))}
  public final @NotNull Resource ${AbstractResourceFactoryGen.factoryMethodName(rd)}() throws ServiceInitializationException {
    return ${sp(ServiceGenUtils.INDENT * 2, resourceConstructor)}
  }

  ${i(ServiceGenUtils.genMethods(sgctx))}
}
"""/*@formatter:on*/

  }

  private def generateResourceConstructor(ctx: ServiceGenContext): String = {

    val resourceDeclarationClassName = ResourceDeclarationGen.resourceDeclarationClassName(rd)
//    ctx.addImport(ResourceDeclarationGen.resourceDeclarationNamespace(baseNamespace, rd).append(resourceDeclarationClassName))

    var readOperationConstructorCalls: List[String] = List()
    var updateOperationConstructorCalls: List[String] = List()
    var createOperationConstructorCalls: List[String] = List()
    var deleteOperationConstructorCalls: List[String] = List()
    var customOperationConstructorCalls: List[String] = List()

    for (od <- rd.operations()) {
      val declarationField = ResourceDeclarationGen.resourceDeclarationClassName(rd) +
                             "." + ResourceDeclarationGen.operationDeclarationFieldName(od)

      od match {

        case o: ReadOperationDeclaration =>
          ctx.addImport("ws.epigraph.service.operations.ReadOperation")

          val operationConstructorMethodName =
            if (o.name() != null) s"construct${up(od.name())}ReadOperation"
            else s"constructReadOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              "read",
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          readOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: CreateOperationDeclaration =>
          ctx.addImport("ws.epigraph.service.operations.CreateOperation")

          val operationConstructorMethodName =
            if (o.name() != null) s"construct${up(od.name())}CreateOperation"
            else "constructCreateOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              "create",
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          createOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: UpdateOperationDeclaration =>
          ctx.addImport("ws.epigraph.service.operations.UpdateOperation")

          val operationConstructorMethodName =
            if (o.name() != null) s"construct${up(od.name())}UpdateOperation"
            else "constructUpdateOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              "update",
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          updateOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: DeleteOperationDeclaration =>
          ctx.addImport("ws.epigraph.service.operations.DeleteOperation")

          val operationConstructorMethodName =
            if (o.name() != null) s"construct${up(od.name())}DeleteOperation"
            else "constructDeleteOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              "delete",
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          deleteOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: CustomOperationDeclaration =>
          ctx.addImport("ws.epigraph.service.operations.CustomOperation")

          val operationConstructorMethodName = s"construct${up(od.name())}CustomOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              "custom",
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          customOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case _ => throw new CompilerException
      }
    }

    /*@formatter:off*/sn"""\
new Resource(
  $resourceDeclarationClassName.INSTANCE,
  ${i(genCollection(readOperationConstructorCalls, ctx))},
  ${i(genCollection(createOperationConstructorCalls, ctx))},
  ${i(genCollection(updateOperationConstructorCalls, ctx))},
  ${i(genCollection(deleteOperationConstructorCalls, ctx))},
  ${i(genCollection(customOperationConstructorCalls, ctx))}
);"""/*@formatter:on*/
  }

  private def genOperationConstructorMethod(
    o: OperationDeclaration,
    kind: String,
    resourceDeclarationClassName: String,
    operationConstructorMethodName: String,
    ctx: ServiceGenContext): String = {

    val ukind = up(kind)
    val declarationType = ukind + "OperationDeclaration"
    val methodType: String = s"${ukind}Operation<${ServiceGenUtils.genDataRef(o.outputType(), ctx.gctx)}>"

    ctx.addImport("ws.epigraph.schema.operations." + declarationType)

    // todo abstract op references
    /*@formatter:off*/sn"""\
/**
 * Constructs ${rd.fieldName()} ${if (o.name() !=null ) "'" + up(o.name()) + "' " else ""}$kind operation
 * <p>
 * {@code todo} is a suggested operation implementation base class
 * </p>
 *
 * @param operationDeclaration operation {@link $resourceDeclarationClassName#${ResourceDeclarationGen.operationDeclarationFieldName(o)} declaration}
 *
 * @return operation implementation
 *
 * @throws ServiceInitializationException in case of operation initialization error
 *
 * @see todo todo
 */
protected abstract @NotNull $methodType $operationConstructorMethodName(@NotNull $declarationType operationDeclaration) throws ServiceInitializationException;\
"""/*@formatter:on*/

  }

  private def genCollection(calls: List[String], ctx: ServiceGenContext): String = calls.length match {
    case 0 =>
      ctx.addImport(classOf[java.util.Collections].getCanonicalName)
      "Collections.emptyList()"
    case 1 =>
      ctx.addImport(classOf[java.util.Collections].getCanonicalName)
      "Collections.singletonList(" + calls.head + ")"
    case _ =>
      ctx.addImport(classOf[java.util.Arrays].getCanonicalName)
      calls
        .reverse
        .map{ c => JavaGenUtils.indent(c, ServiceGenUtils.INDENT) }
        .mkString("Arrays.asList(\n", ",\n", "\n)")
  }

}

object AbstractResourceFactoryGen {
  def abstractResourceFactoryNamespace(baseNamespace: Qn, rd: ResourceDeclaration): Qn =
    ServiceNames.resourceNamespace(baseNamespace, rd.fieldName())

  def abstractResourceFactoryClassName(rd: ResourceDeclaration): String =
    s"Abstract${up(rd.fieldName())}ResourceFactory"

  def factoryMethodName(rd: ResourceDeclaration): String = s"get${up(rd.fieldName())}Resource"
}
