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
import ws.epigraph.java.JavaGenNames._
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.CodeChunk
import ws.epigraph.java._
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations._

import scala.collection.JavaConverters._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ResourceClientGen(rd: ResourceDeclaration, baseNamespace: Qn, val ctx: GenContext) extends JavaGen {

  protected val namespace: Qn = ResourceClientGen.clientNamespace(baseNamespace, rd)
  protected val className: String = ResourceClientGen.clientClassName(rd)
  protected val resourceDeclarationClassName: String = ResourceDeclarationGen.resourceDeclarationClassName(rd)

  override def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(ResourceClientGen.clientClassName(rd) + ".java")


  override def generate: String = {
    val sgctx = new ObjectGenContext(ctx, namespace)

    val clientsParts: Seq[OpClientParts] = rd.operations().asScala.map(op => genOpClient(op, sgctx))

    // todo rework to use imported (short) class names
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
      "ws.epigraph.invocation.OperationInvocationException",
      "ws.epigraph.refs.StaticTypesResolver",
      "ws.epigraph.refs.TypesResolver",
      "ws.epigraph.schema.operations.OperationDeclaration",
      "ws.epigraph.util.EBean",
      "ws.epigraph.wire.json.JsonFormatFactories",
      "java.nio.charset.Charset",
      "java.nio.charset.StandardCharsets",
      "java.util.concurrent.CompletableFuture"
    ).foreach(i => sgctx.use(i))

    clientsParts.foreach { ocp => sgctx.addField(s"private final ${ ocp.invFieldType } ${ ocp.invFieldName };") }

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ObjectGenUtils.genImports(sgctx)}

/**
 * {@code ${rd.fieldName()}} resource client
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $className {
  private final boolean debug;
  private final TypesResolver typesResolver;
  private final OperationFilterChains<Data> filterChains;

  ${i(ObjectGenUtils.genFields(sgctx))}

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
            StaticTypesResolver.instance()
        ),
        StandardCharsets.UTF_8,
        StaticTypesResolver.instance(),
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

  private def genOpClient(op: OperationDeclaration, sgctx: ObjectGenContext): OpClientParts = {
    val kind = op.kind().toString.toLowerCase()
    val name = op.name()

    val isDefault = name == null
    val nameSuffix = if (isDefault) "" else up(name)

    val fieldName = kind + nameSuffix + "Inv"
    val methodName = kind + nameSuffix

    val outType = JavaGenUtils.toCType(op.outputType())
    val outTypeExpr = lqn2(outType, namespace.toString)
    val outDataTypeExpr = lqdrn2(outType, namespace.toString)

    lazy val inType = JavaGenUtils.toCType(op.inputType())
    lazy val inTypeExpr = lqn2(inType, namespace.toString)
//    lazy val inDataTypeExpr = lqdrn2(inType, namespace.toString)

    lazy val pathJavadoc = if (op.path() == null) "" else "   * @param path              path expression\n"
    lazy val pathParam = if (op.path() == null) "" else "      @NotNull String path,\n"

    val resDeclInstance = s"${
      ResourceDeclarationGen.resourceDeclarationNamespace(baseNamespace, rd)
    }.${ ResourceDeclarationGen.resourceDeclarationClassName(rd) }.INSTANCE"

    val opDecl = s"${
      ResourceDeclarationGen.resourceDeclarationNamespace(baseNamespace, rd)
    }.${ ResourceDeclarationGen.resourceDeclarationClassName(rd) }.${
      ResourceDeclarationGen.operationDeclarationFieldName(op)
    }"

    def fieldInitExpr(kind: String): String = /*@formatter:off*/sn"""\
    $fieldName = new Remote${kind}OperationInvocation(
      host,
      httpClient,
      $resDeclInstance.fieldName(),
      $opDecl,
      serverProtocol,
      charset
    );"""/*@formatter:on*/

    op.kind() match {
      case OperationKind.READ => //                                                                                 READ
        sgctx.use("ws.epigraph.client.http.RemoteReadOperationInvocation")
        sgctx.use("ws.epigraph.service.operations.ReadOperationRequest")
        sgctx.use("ws.epigraph.service.operations.ReadOperationResponse")

        val fieldType = "RemoteReadOperationInvocation"
        val fieldInit = fieldInitExpr("Read")

        val method = /*@formatter:off*/sn"""\
  /**
   * Invokes ${if (isDefault) "default" else s"'$name'"} read operation
   *
   * @param projection output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(@NotNull String projection) throws OperationInvocationException {
    OperationInvocationContext ctx = newInvocationContext($opDecl);

    ReadOperationRequest request = RequestFactory.constructReadRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        projection,
        typesResolver
    );

    return filterChains
        .filterRead($fieldName)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.ENTITY) "data" else "data == null ? null : data.get()"};
        }).get());
  }"""/*@formatter:on*/

        OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

      case OperationKind.CREATE => //                                                                             CREATE
        sgctx.use("ws.epigraph.client.http.RemoteCreateOperationInvocation")
        sgctx.use("ws.epigraph.service.operations.CreateOperationRequest")

        // one less space for better formatting
        val pathJavadoc = if (op.path() == null) "" else "   * @param path             path expression\n"
        val fieldType = "RemoteCreateOperationInvocation"
        val fieldInit = fieldInitExpr("Create")

        val method = /*@formatter:off*/sn"""\
  /**
   * Invokes ${if (isDefault) "default" else s"'$name'"} create operation
   *
$pathJavadoc   * @param inputProjection  (optional) input projection
   * @param inputData        operation input data
   * @param outputProjection output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(
$pathParam      @Nullable String inputProjection,
      @NotNull $inTypeExpr inputData,
      @NotNull String outputProjection) throws OperationInvocationException {

    OperationInvocationContext ctx = newInvocationContext($opDecl);

    CreateOperationRequest request = RequestFactory.constructCreateRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        ${if (op.path() == null) "null" else "path"},
        inputProjection,
        ${if (inType.kind == CTypeKind.ENTITY) "inputData" else s"$inTypeExpr.type.createDataBuilder().set(inputData)"},
        outputProjection,
        typesResolver
    );

    return filterChains
        .filterCreate($fieldName)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.ENTITY) "data" else "data == null ? null : data.get()"};
        }).get());
  }"""/*@formatter:on*/

        OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

      case OperationKind.UPDATE => //                                                                             UPDATE
        sgctx.use("ws.epigraph.client.http.RemoteUpdateOperationInvocation")
        sgctx.use("ws.epigraph.service.operations.UpdateOperationRequest")

        val fieldType = "RemoteUpdateOperationInvocation"
        val fieldInit = fieldInitExpr("Update")

        val method = /*@formatter:off*/sn"""\
  /**
   * Invokes ${if (isDefault) "default" else s"'$name'"} update operation
   *
$pathJavadoc   * @param updateProjection  (optional) update projection
   * @param updateData        operation update data
   * @param outputProjection  output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(
$pathParam      @Nullable String updateProjection,
      @NotNull $inTypeExpr updateData,
      @NotNull String outputProjection) throws OperationInvocationException {

    OperationInvocationContext ctx = newInvocationContext($opDecl);

    UpdateOperationRequest request = RequestFactory.constructUpdateRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        ${if (op.path() == null) "null" else "path"},
        updateProjection,
        ${if (inType.kind == CTypeKind.ENTITY) "updateData" else s"$inTypeExpr.type.createDataBuilder().set(updateData)"},
        outputProjection,
        typesResolver
    );

    return filterChains
        .filterUpdate($fieldName)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.ENTITY) "data" else "data == null ? null : data.get()"};
        }).get());
  }"""/*@formatter:on*/

        OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

      case OperationKind.DELETE => //                                                                             DELETE
        sgctx.use("ws.epigraph.client.http.RemoteDeleteOperationInvocation")
        sgctx.use("ws.epigraph.service.operations.DeleteOperationRequest")

        val fieldType = "RemoteDeleteOperationInvocation"
        val fieldInit = fieldInitExpr("Delete")
        // one less space for better formatting
        val pathJavadoc = if (op.path() == null) "" else "   * @param path             path expression\n"

        val method = /*@formatter:off*/sn"""\
  /**
   * Invokes ${if (isDefault) "default" else s"'$name'"} delete operation
   *
$pathJavadoc   * @param deleteProjection delete projection
   * @param outputProjection output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(
$pathParam      @NotNull String deleteProjection,
      @NotNull String outputProjection) throws OperationInvocationException {

    OperationInvocationContext ctx = newInvocationContext($opDecl);

    DeleteOperationRequest request = RequestFactory.constructDeleteRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        ${if (op.path() == null) "null" else "path"},
        deleteProjection,
        outputProjection,
        typesResolver
    );

    return filterChains
        .filterDelete($fieldName)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.ENTITY) "data" else "data == null ? null : data.get()"};
        }).get());
  }"""/*@formatter:on*/

        OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

      case OperationKind.CUSTOM => //                                                                             CUSTOM
        sgctx.use("ws.epigraph.client.http.RemoteCustomOperationInvocation")
        sgctx.use("ws.epigraph.service.operations.CustomOperationRequest")

        // one less space for better formatting
        val pathJavadoc = if (op.path() == null) "" else "   * @param path             path expression\n"
        val fieldType = "RemoteCustomOperationInvocation"
        val fieldInit = fieldInitExpr("Custom")

        val invCode = /*@formatter:off*/sn"""\
    return filterChains
        .filterCustom($fieldName)
        .invoke(request, ctx).thenApply(oir -> oir.mapSuccess(ror -> {
          $outDataTypeExpr data = ($outDataTypeExpr) ror.getData();
          return ${if (outType.kind == CTypeKind.ENTITY) "data" else "data == null ? null : data.get()"};
        }).get());"""/*@formatter:on*/

        // two options here: with and without input data

        if (op.inputType() == null) { //                                                       CUSTOM WITHOUT INPUT DATA
          val method = /*@formatter:off*/sn"""\
  /**
   * Invokes '$name' custom operation
   *
$pathJavadoc   * @param outputProjection output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(
$pathParam      @NotNull String outputProjection) throws OperationInvocationException {

    OperationInvocationContext ctx = newInvocationContext($opDecl);

    CustomOperationRequest request = RequestFactory.constructCustomRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        ${if (op.path() == null) "null" else "path"},
        null,
        null,
        outputProjection,
        typesResolver
    );

$invCode
  }"""/*@formatter:on*/

          OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

        } else {//                                                                                CUSTOM WITH INPUT DATA

          val method = /*@formatter:off*/sn"""\
  /**
   * Invokes '$name' custom operation
   *
$pathJavadoc   * @param inputProjection  (optional) input projection
   * @param inputData        operation input data
   * @param outputProjection output projection
   *
   * @return future of invocation result
   * @throws OperationInvocationException in case of invocation error
   */
  public @NotNull CompletableFuture<$outTypeExpr> $methodName(
$pathParam      @Nullable String inputProjection,
      @Nullable $inTypeExpr inputData,
      @NotNull String outputProjection) throws OperationInvocationException {

    OperationInvocationContext ctx = newInvocationContext($opDecl);

    CustomOperationRequest request = RequestFactory.constructCustomRequest(
        $resDeclInstance.fieldType(),
        $opDecl,
        ${if (op.path() == null) "null" else "path"},
        inputProjection,
        ${if (inType.kind == CTypeKind.ENTITY) "inputData" else s"inputData == null ? null : $inTypeExpr.type.createDataBuilder().set(inputData)"},
        outputProjection,
        typesResolver
    );

$invCode
  }"""/*@formatter:on*/

          OpClientParts(fieldType, fieldName, CodeChunk(fieldInit), CodeChunk(method))

        }

      case _ => throw new RuntimeException("Unknown operation kind: " + op.kind().toString)
    }
  }

  case class OpClientParts(invFieldType: String, invFieldName: String, invFieldInit: CodeChunk, method: CodeChunk)

}

object ResourceClientGen {
  def clientNamespace(baseNamespace: Qn, rd: ResourceDeclaration): Qn =
    ServiceNames.clientNamespace(baseNamespace, rd.fieldName())

  def clientClassName(rd: ResourceDeclaration): String = up(rd.fieldName()) + "Client"
}
