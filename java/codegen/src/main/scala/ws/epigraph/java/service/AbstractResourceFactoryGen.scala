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
import ws.epigraph.java._
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
    rd.operations().map {
      case o: ReadOperationDeclaration => o -> new AbstractReadOperationGen(baseNamespace, rd, o, ctx)
      case o: CreateOperationDeclaration => o -> new AbstractCreateOperationGen(baseNamespace, rd, o, ctx)
      case o: UpdateOperationDeclaration => o -> new AbstractUpdateOperationGen(baseNamespace, rd, o, ctx)
      case o: DeleteOperationDeclaration => o -> new AbstractDeleteOperationGen(baseNamespace, rd, o, ctx)
      case o: CustomOperationDeclaration => o -> new AbstractCustomOperationGen(baseNamespace, rd, o, ctx)
      case other => throw new IllegalArgumentException(s"Unknown operation declaration class: ${ other.getClass.getName }")
    }.toMap


  override def children: Iterable[JavaGen] = super.children ++ abstractOperationGens.values

  override def generate: String = {
    val sgctx = new ObjectGenContext(ctx, namespace)
    val className = AbstractResourceFactoryGen.abstractResourceFactoryClassName(rd)

    val notnull = sgctx.use("org.jetbrains.annotations.NotNull")
    val resource = sgctx.use("ws.epigraph.service.Resource")
    val siexc = sgctx.use("ws.epigraph.service.ServiceInitializationException")

    val resourceConstructor = generateResourceConstructor(sgctx)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ObjectGenUtils.genImports(sgctx)}
/**
 * Abstract factory for constructing {@code ${rd.fieldName()}} resource implementation
 */
public abstract class $className {
  ${i(ObjectGenUtils.genFields(sgctx))}
  public final @$notnull $resource ${AbstractResourceFactoryGen.factoryMethodName(rd)}() throws $siexc {
    return ${sp(ObjectGenUtils.INDENT * 2, resourceConstructor)}
  }

  ${i(ObjectGenUtils.genMethods(sgctx))}
}
"""/*@formatter:on*/

  }

  private def generateResourceConstructor(ctx: ObjectGenContext): String = {

    val resourceDeclarationClassName = ResourceDeclarationGen.resourceDeclarationClassName(rd)

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
          val operationConstructorMethodName =
            if (o.name() != null) s"construct${ up(od.name()) }ReadOperation"
            else s"constructReadOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          readOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: CreateOperationDeclaration =>
          val operationConstructorMethodName =
            if (o.name() != null) s"construct${ up(od.name()) }CreateOperation"
            else "constructCreateOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          createOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: UpdateOperationDeclaration =>
          val operationConstructorMethodName =
            if (o.name() != null) s"construct${ up(od.name()) }UpdateOperation"
            else "constructUpdateOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          updateOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: DeleteOperationDeclaration =>
          val operationConstructorMethodName =
            if (o.name() != null) s"construct${ up(od.name()) }DeleteOperation"
            else "constructDeleteOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
              resourceDeclarationClassName,
              operationConstructorMethodName,
              ctx
            )
          )

          deleteOperationConstructorCalls ::= s"$operationConstructorMethodName($declarationField)"

        case o: CustomOperationDeclaration =>
          val operationConstructorMethodName = s"construct${ up(od.name()) }CustomOperation"

          ctx.addMethod(
            genOperationConstructorMethod(
              o,
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
new ${ctx.use("ws.epigraph.service.Resource")}(
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
    resourceDeclarationClassName: String,
    operationConstructorMethodName: String,
    ctx: ObjectGenContext): String = {

    val kind = ServiceNames.operationKinds(o.kind())
    val ukind = up(kind)
    val operation: String = ctx.use(s"ws.epigraph.service.operations.${ ukind }Operation")
    val methodType: String = s"$operation<${ ObjectGenUtils.genDataRef(o.outputType(), ctx.gctx) }>"

    val notnull = ctx.use("org.jetbrains.annotations.NotNull")
    val declarationType = ctx.use("ws.epigraph.schema.operations." + ukind + "OperationDeclaration")
    val siexc = ctx.use("ws.epigraph.service.ServiceInitializationException")

    val abstractOpGen: AbstractOperationGen = abstractOperationGens(o)

    /*@formatter:off*/sn"""\
/**
 * Constructs ${rd.fieldName()} ${if (o.name() !=null ) "'" + up(o.name()) + "' " else ""}$kind operation
 * <p>
 * {@code ${abstractOpGen.shortClassName}} is a suggested operation implementation base class
 *
 * @param operationDeclaration operation {@link $resourceDeclarationClassName#${ResourceDeclarationGen.operationDeclarationFieldName(o)} declaration}
 *
 * @return operation implementation
 *
 * @throws $siexc in case of operation initialization error
 *
 * @see ${abstractOpGen.namespace}.${abstractOpGen.shortClassName} ${abstractOpGen.shortClassName}
 */
protected abstract @$notnull $methodType $operationConstructorMethodName(@$notnull $declarationType operationDeclaration) throws $siexc;\
"""/*@formatter:on*/

  }

  private def genCollection(calls: List[String], ctx: ObjectGenContext): String = calls.length match {
    case 0 =>
      ctx.use(classOf[java.util.Collections].getCanonicalName) + ".emptyList()"
    case 1 =>
      ctx.use(classOf[java.util.Collections].getCanonicalName) + ".singletonList(" + calls.head + ")"
    case _ =>
      val arr = ctx.use(classOf[java.util.Arrays].getCanonicalName)
      calls
        .reverse
        .map { c => JavaGenUtils.indent(c, ObjectGenUtils.INDENT) }
        .mkString(s"$arr.asList(\n", ",\n", "\n)")
  }

}

object AbstractResourceFactoryGen {
  def abstractResourceFactoryNamespace(baseNamespace: Qn, rd: ResourceDeclaration): Qn =
    ServiceNames.resourceNamespace(baseNamespace, rd.fieldName())

  def abstractResourceFactoryClassName(rd: ResourceDeclaration): String =
    s"Abstract${ up(rd.fieldName()) }ResourceFactory"

  def factoryMethodName(rd: ResourceDeclaration): String = s"get${ up(rd.fieldName()) }Resource"
}
