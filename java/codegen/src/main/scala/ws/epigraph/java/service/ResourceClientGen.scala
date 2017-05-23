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

import ws.epigraph.compiler.CTypeKind
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.CodeChunk
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations._
import ws.epigraph.java.JavaGenNames._

import scala.collection.JavaConverters._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ResourceClientGen(rd: ResourceDeclaration, baseNamespace: Qn, val ctx: GenContext) extends JavaGen {

  protected val namespace: Qn = ResourceClientGen.clientNamespace(baseNamespace, rd)
  protected val className: String = ResourceClientGen.clientClassName(rd)
  protected val resourceDeclarationClassName: String = ResourceDeclarationGen.resourceDeclarationClassName(rd)

  override protected def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(ResourceClientGen.clientClassName(rd) + ".java")


  override def generate: String = {
    val sgctx = new ServiceGenContext(ctx)

    val clientsParts: Seq[OpClientParts] = rd.operations().asScala.map(op => genOpClient(op, sgctx))

    List(
      "org.apache.http.HttpHost",
      "org.apache.http.nio.client.HttpAsyncClient",
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "ws.epigraph.client.http.FormatBasedServerProtocol",
      "ws.epigraph.client.http.RequestFactory",
      "ws.epigraph.client.http.ServerProtocol",
      "ws.epigraph.data.Data",
      "ws.epigraph.invocation.DefaultOperationInvocationContext",
      "ws.epigraph.invocation.OperationFilterChains",
      "ws.epigraph.invocation.OperationInvocationContext",
      "ws.epigraph.invocation.OperationInvocationResult",
      "ws.epigraph.refs.IndexBasedTypesResolver",
      "ws.epigraph.refs.TypesResolver",
      "ws.epigraph.schema.operations.OperationDeclaration",
      "ws.epigraph.util.EBean",
      "ws.epigraph.wire.json.JsonFormatFactories",
      "java.nio.charset.Charset",
      "java.nio.charset.StandardCharsets",
      "java.util.concurrent.CompletableFuture"
    ).foreach(i => sgctx.addImport(i))

    clientsParts.foreach { ocp => sgctx.addField(s"private final ${ ocp.invFieldType } ${ ocp.invFieldName };") }

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ServiceGenUtils.genImports(sgctx)}

/**
 * {@code ${rd.fieldName()}} resource client
 */
public class $className {
  private final boolean debug;
  private final TypesResolver typesResolver;
  private final OperationFilterChains<Data> filterChains;
  ${i(ServiceGenUtils.genFields(sgctx))}

  /**
   * Creates new client
   * <p>
   * Client will be using JSON protocol and default request filters
   *
   * @param host       target host
   * @param httpClient HTTP client instance
   */
  public $className(
      @NotNull HttpHost host,
      @NotNull HttpAsyncClient httpClient) {

    this(
        host,
        httpClient,
        new FormatBasedServerProtocol(
            JsonFormatFactories.INSTANCE,
            StandardCharsets.UTF_8,
            IndexBasedTypesResolver.INSTANCE,
        ),
        StandardCharsets.UTF_8,
        IndexBasedTypesResolver.INSTANCE,
        OperationFilterChains.defaultFilterChains(),
        false
    );
  }

  /**
   * Creates new client
   *
   * @param host           target host
   * @param httpClient     HTTP client instance
   * @param serverProtocol server protocol
   * @param charset        charset (should be in sync with {@code serverProtocol})
   * @param typesResolver  types resolver
   * @param filterChains   request filter chains
   * @param debug          if debug mode should be used
   */
  public $className(
      @NotNull HttpHost host,
      @NotNull HttpAsyncClient httpClient,
      @NotNull ServerProtocol serverProtocol,
      @NotNull Charset charset,
      @NotNull TypesResolver typesResolver,
      @NotNull OperationFilterChains<Data> filterChains,
      boolean debug) {

    this.typesResolver = typesResolver;
    this.debug = debug;
    this.filterChains = filterChains;

${clientsParts.map(cp=>cp.invFieldInit).foldLeft(CodeChunk.empty)(_+_).code}
  }

${clientsParts.map(cp=>cp.method).foldLeft(CodeChunk.empty)(_+_).code}

  /**
   * Creates new operation invocation context for a given operation
   *
   * @param op target operation declaration
   *
   * @return operation invocation context
   */
  protected @NotNull OperationInvocationContext newInvocationContext(@NotNull OperationDeclaration op) {
    return new DefaultOperationInvocationContext(debug, newContextStorage(op));
  }

  /**
   * Creates new operation invocation context custom storage for a given operation
   *
   * @param op target operation declaration
   *
   * @return operation invocation context custom storage
   */
  protected @NotNull EBean newContextStorage(@NotNull OperationDeclaration op) { return new EBean(); }
}
"""/*@formatter:on*/
  }

  private def genOpClient(op: OperationDeclaration, sgctx: ServiceGenContext): OpClientParts = {
    val kind = op.kind().toString.toLowerCase()
    val name = op.name()

    val isDefault = name == null
    val nameSuffix = if (isDefault) "" else up(name)

    val fieldName = kind + nameSuffix + "Inv"
    val methodName = kind + nameSuffix

    val outType = JavaGenUtils.toCType(op.outputType())
    val outTypeExpr = lqn2(outType, namespace.toString)
    val outDataTypeExpr = lqdrn2(outType, namespace.toString)

    val resDeclInstance = s"${
      ResourceDeclarationGen.resourceDeclarationNamespace(
        baseNamespace,
        rd
      )
    }.${ ResourceDeclarationGen.resourceDeclarationClassName(rd) }.INSTANCE"
    val opDecl = s"${
      ResourceDeclarationGen.resourceDeclarationNamespace(
        baseNamespace,
        rd
      )
    }.${ ResourceDeclarationGen.resourceDeclarationClassName(rd) }.${
      ResourceDeclarationGen.operationDeclarationFieldName(
        op
      )
    }"

    op.kind() match {
      case OperationKind.READ =>
        sgctx.addImport("ws.epigraph.client.http.RemoteReadOperationInvocation")
        sgctx.addImport("ws.epigraph.service.operations.ReadOperationRequest")
        sgctx.addImport("ws.epigraph.service.operations.ReadOperationResponse")

        val fieldType = "RemoteReadOperationInvocation"

        val fieldInit = /*@formatter:off*/sn"""\
    $fieldName = new RemoteReadOperationInvocation(
      host,
      httpClient,
      $resDeclInstance.fieldName(),
      $opDecl,
      serverProtocol,
      charset
    );"""/*@formatter:on*/



        val method = /*@formatter:off*/sn"""\
  /**
   * Invokes ${if (isDefault) "default" else s"'$name'"} read operation
   *
   * @param projection output projection
   *
   * @return future of invocation result
   */
  public @NotNull CompletableFuture<@NotNull OperationInvocationResult<@Nullable $outTypeExpr>> $methodName(@NotNull String projection) {
    OperationInvocationContext ctx = newInvocationContext($opDecl);

    ReadOperationRequest request = RequestFactory.constructReadRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        projection,
        typesResolver
    );

    return filterChains
        .filterRead(readInv)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.VARTYPE) "data" else "data == null ? null : data.get()"};
        }));
  }"""/*@formatter:on*/

        OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

      case _ =>
        // todo remove me
        OpClientParts("void", "fixme", CodeChunk.empty, CodeChunk.empty)
    }
  }

  case class OpClientParts(invFieldType: String, invFieldName: String, invFieldInit: CodeChunk, method: CodeChunk)

}

object ResourceClientGen {
  def clientNamespace(baseNamespace: Qn, rd: ResourceDeclaration): Qn =
    ServiceNames.clientNamespace(baseNamespace, rd.fieldName())

  def clientClassName(rd: ResourceDeclaration): String = up(rd.fieldName()) + "Client"
}
