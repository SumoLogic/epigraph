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

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.*;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.input.ReqInputModelProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.projections.req.update.ReqUpdateModelProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.*;
import ws.epigraph.service.operations.*;
import ws.epigraph.url.*;
import ws.epigraph.url.parser.CustomRequestUrlPsiParser;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.util.HttpStatusCode;
import ws.epigraph.wire.*;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractHttpServer<C extends InvocationContext> {
  private static final Pattern RESOURCE_PATTERN = Pattern.compile("/(\\p{Lower}\\p{Alnum}*)(.*)");

  protected final @NotNull Service service;
  protected final @NotNull OperationFilterChains<Data> operationFilterChains;

  @SuppressWarnings("unchecked")
  protected AbstractHttpServer(
      final @NotNull Service service,
      final @NotNull OperationFilterChains<? extends Data> invocations) {
    this.service = service;
    operationFilterChains = (OperationFilterChains<Data>) invocations;
  }

  protected abstract long responseTimeout(@NotNull C context);

  protected abstract OpInputFormatReader opInputReader(@NotNull C context) throws IOException;

  protected abstract ReqInputFormatReader reqInputReader(@NotNull C context) throws IOException;

  protected abstract ReqUpdateFormatReader reqUpdateReader(@NotNull C context) throws IOException;

  protected abstract void writeFormatResponse(
      int statusCode,
      @NotNull C context,
      @NotNull FormatResponseWriter formatWriter);

  protected abstract void writeInvocationErrorResponse(@NotNull OperationInvocationError error, @NotNull C context);

  protected void close(@NotNull C context) throws IOException { }

  // -----------------------------------


  protected void handleRequest(
      @NotNull String decodedUri,
      @NotNull HttpMethod requestMethod,
      @Nullable String operationName,
      @NotNull C context) {

    // extract resource name from URI
    Matcher matcher = RESOURCE_PATTERN.matcher(decodedUri);
    if (!matcher.matches()) {
      writeGenericErrorAndClose(
          String.format(
              "Bad URL format. Supported resources: {%s}",
              Util.listSupportedResources(service)
          ), HttpStatusCode.BAD_REQUEST, context
      );
      return;
    }
    String resourceName = matcher.group(1);

    // find resource by name
    final Resource resource;
    try {
      resource = ResourceRouter.findResource(resourceName, service);
    } catch (ResourceNotFoundException ignored) {
      writeGenericErrorAndClose(
          String.format(
              "Resource '%s' not found. Supported resources: {%s}",
              resourceName,
              Util.listSupportedResources(service)
          ), HttpStatusCode.BAD_REQUEST, context
      );
      return;
    }

    if (operationName != null) {
      CustomOperation<?> customOperation = resource.customOperation(requestMethod, operationName);
      if (customOperation != null) {
        handleCustomRequest(
            resource,
            operationName,
            requestMethod,
            decodedUri,
            context
        );
        return;
      }
    }

    if (requestMethod == HttpMethod.GET)
      handleReadRequest(resource, operationName, decodedUri, context);
    else if (requestMethod == HttpMethod.POST)
      handleCreateRequest(resource, operationName, decodedUri, context);
    else if (requestMethod == HttpMethod.PUT)
      handleUpdateRequest(resource, operationName, decodedUri, context);
    else if (requestMethod == HttpMethod.DELETE)
      handleDeleteRequest(resource, operationName, decodedUri, context);
    else {
      writeGenericErrorAndClose(
          String.format(
              "Unsupported HTTP method '%s'",
              requestMethod
          ), HttpStatusCode.BAD_REQUEST, context
      );
    }

  }

  // ----------------------------------- READ

  protected void handleReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(HttpStatusCode.OK.code(), invokeReadRequest(resource, operationName, decodedUri, context), context);
  }

  private static final class ReadResult {
    final @Nullable Data data;
    final int pathSteps;
    final @NotNull ReqOutputVarProjection projection;

    ReadResult(
        final @Nullable Data data,
        final int steps,
        final @NotNull ReqOutputVarProjection projection) {
      this.data = data;
      pathSteps = steps;
      this.projection = projection;
    }
  }

  @SuppressWarnings("unchecked")
  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedReadOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlReadUrl urlPsi = parseReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withReadOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          ReadRequestUrl requestUrl = operationSearchResult.requestUrl();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          ReadOperation<Data> operation = operationSearchResult.operation();

          OperationInvocation<ReadOperationRequest, ReadOperationResponse<Data>> operationInvocation =
              operationFilterChains.readOperationInvocation(operation);

          return operationInvocation.invoke(
              new ReadOperationRequest(
                  requestUrl.path(),
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlReadUrl parseReadUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.READ_URL.rootElementType(),
        UrlReadUrl.class,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withReadOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlReadUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<ReadOperation<Data>, ReadRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        ReadOperationRouter.INSTANCE,
        OperationKind.READ,
        continuation,
        context
    );
  }

  private void handleReadResponse(
      final int statusCode,
      @NotNull CompletionStage<OperationInvocationResult<ReadResult>> responseFuture,
      @NotNull C context) {

    final Consumer<OperationInvocationResult<ReadResult>> resultConsumer =
        invocationResult ->
            invocationResult.consume(

                readResult -> {
                  try {
                    writeData(statusCode, readResult.pathSteps, readResult.projection, readResult.data, context);
                  } catch (RuntimeException e) {
                    writeInvocationErrorAndCloseContext(
                        new GenericServerInvocationError(e.toString()),
                        context
                    );
                  }
                },

                error -> writeInvocationErrorAndCloseContext(error, context)
            );

    final Function<Throwable, Void> failureConsumer = throwable -> {
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(throwable.getMessage()), context
      );
      return null;
    };

    long timeout = responseTimeout(context);
    if (timeout > 0) {
      CompletionStage<OperationInvocationResult<ReadResult>> timeoutStage = Util.failAfter(Duration.ofMillis(timeout));
      responseFuture.acceptEither(timeoutStage, resultConsumer).exceptionally(failureConsumer);
    } else {
      responseFuture.thenAccept(resultConsumer).exceptionally(failureConsumer);
    }
  }

  // ----------------------------------- CREATE

  protected void handleCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(
        HttpStatusCode.CREATED.code(),
        invokeCreateRequest(resource, operationName, decodedUri, context),
        context
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedCreateOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.CREATE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlCreateUrl urlPsi = parseCreateUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CREATE,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withCreateOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          @NotNull CreateRequestUrl requestUrl = operationSearchResult.requestUrl();
          ReqInputFieldProjection inputProjection = requestUrl.inputProjection();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull CreateOperation<Data> operation = operationSearchResult.operation();

          final Data body;
          try {
            if (inputProjection == null) {
              FormatReader<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> reader = opInputReader(context);
              body = reader.readData(operation.declaration().inputProjection().varProjection());
            } else {
              FormatReader<ReqInputVarProjection, ReqInputModelProjection<?, ?, ?>> reader = reqInputReader(context);
              body = reader.readData(inputProjection.varProjection());
            }
          } catch (FormatException | IOException e) {
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(
                    new MalformedInputInvocationError(String.format(
                        "Error reading %screate request body: %s",
                        operationName == null ? "" : "'" + operationName + "'",
                        e.getMessage()
                    ))
                )
            );
          }

          if (body == null)
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(new MalformedInputInvocationError("Null body for create operation"))
            );

          OperationInvocation<CreateOperationRequest, ReadOperationResponse<Data>> operationInvocation =
              operationFilterChains.createOperationInvocation(operation);

          return operationInvocation.invoke(
              new CreateOperationRequest(
                  requestUrl.path(),
                  body,
                  inputProjection,
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlCreateUrl parseCreateUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlCreateUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.CREATE_URL.rootElementType(),
        UrlCreateUrl.class,
        UrlSubParserDefinitions.CREATE_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withCreateOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlCreateUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<CreateOperation<Data>, CreateRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        CreateOperationRouter.INSTANCE,
        OperationKind.CREATE,
        continuation,
        context
    );
  }

  // ----------------------------------- UPDATE

  protected void handleUpdateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(
        HttpStatusCode.OK.code(),
        invokeUpdateRequest(resource, operationName, decodedUri, context),
        context
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeUpdateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedUpdateOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.UPDATE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUpdateUrl urlPsi = parseUpdateUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.UPDATE,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withUpdateOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          @NotNull UpdateRequestUrl requestUrl = operationSearchResult.requestUrl();
          @Nullable ReqUpdateFieldProjection updateProjection = requestUrl.updateProjection();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull UpdateOperation<Data> operation = operationSearchResult.operation();

          final Data body;
          try {
            if (updateProjection == null) {
              FormatReader<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> reader = opInputReader(context);
              body = reader.readData(operation.declaration().inputProjection().varProjection());
            } else {
              @NotNull FormatReader<ReqUpdateVarProjection, ReqUpdateModelProjection<?, ?, ?>> reader =
                  reqUpdateReader(context);
              body = reader.readData(updateProjection.varProjection());
            }
          } catch (FormatException | IOException e) {
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(
                    new MalformedInputInvocationError(String.format(
                        "Error reading %supdate request body: %s",
                        operationName == null ? "" : "'" + operationName + "'",
                        e.getMessage()
                    ))
                )
            );
          }

          if (body == null)
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(new MalformedInputInvocationError("Null body for update operation"))
            );

          OperationInvocation<UpdateOperationRequest, ReadOperationResponse<Data>> operationInvocation =
              operationFilterChains.updateOperationInvocation(operation);

          return operationInvocation.invoke(
              new UpdateOperationRequest(
                  requestUrl.path(),
                  body,
                  updateProjection,
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlUpdateUrl parseUpdateUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlUpdateUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.UPDATE_URL.rootElementType(),
        UrlUpdateUrl.class,
        UrlSubParserDefinitions.UPDATE_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withUpdateOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUpdateUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<UpdateOperation<Data>, UpdateRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        UpdateOperationRouter.INSTANCE,
        OperationKind.UPDATE,
        continuation,
        context
    );
  }

  // ----------------------------------- DELETE

  protected void handleDeleteRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(
        HttpStatusCode.OK.code(),
        invokeDeleteRequest(resource, operationName, decodedUri, context),
        context
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeDeleteRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedDeleteOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.DELETE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlDeleteUrl urlPsi = parseDeleteUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.DELETE,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withDeleteOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          @NotNull DeleteRequestUrl requestUrl = operationSearchResult.requestUrl();
          @Nullable ReqDeleteFieldProjection deleteProjection = requestUrl.deleteProjection();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull DeleteOperation<Data> operation = operationSearchResult.operation();

          OperationInvocation<DeleteOperationRequest, ReadOperationResponse<Data>> operationInvocation =
              operationFilterChains.deleteOperationInvocation(operation);

          return operationInvocation.invoke(
              new DeleteOperationRequest(
                  requestUrl.path(),
                  deleteProjection,
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlDeleteUrl parseDeleteUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlDeleteUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.DELETE_URL.rootElementType(),
        UrlDeleteUrl.class,
        UrlSubParserDefinitions.DELETE_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withDeleteOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlDeleteUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<DeleteOperation<Data>, DeleteRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        DeleteOperationRouter.INSTANCE,
        OperationKind.DELETE,
        continuation,
        context
    );
  }

  // ----------------------------------- CUSTOM

  protected void handleCustomRequest(
      @NotNull Resource resource,
      @NotNull String operationName,
      @NotNull HttpMethod method,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(
        HttpStatusCode.OK.code(),
        invokeCustomRequest(resource, operationName, method, decodedUri, context),
        context
    );
  }

  @SuppressWarnings("unchecked")
  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeCustomRequest(
      @NotNull Resource resource,
      @NotNull String operationName,
      @NotNull HttpMethod method,
      @NotNull String decodedUri,
      @NotNull C context) {

    final CustomOperation<Data> operation = (CustomOperation<Data>) resource.customOperation(method, operationName);
    if (operation == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  method,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlCustomUrl urlPsi = parseCustomUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    CustomRequestUrl requestUrl = null;
    PsiProcessingContext psiProcessingContext = new DefaultPsiProcessingContext();
    try {
      requestUrl = CustomRequestUrlPsiParser.parseCustomRequestUrl(
          resource.declaration().fieldType(),
          operation.declaration(),
          urlPsi,
          context.typesResolver(),
          psiProcessingContext
      );
    } catch (PsiProcessingException e) {
      psiProcessingContext.setErrors(e.errors());
    }

    if (!psiProcessingContext.errors().isEmpty()) {
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  operationName,
                  decodedUri,
                  psiProcessingContext.errors()
              )
          )
      );
    }

    assert requestUrl != null;

    @Nullable ReqInputFieldProjection inputProjection = requestUrl.inputProjection();
    StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

    Data body;
    try {
      if (inputProjection == null) {
        final OpInputFieldProjection opInputProjection = operation.declaration().inputProjection();
        if (opInputProjection == null)
          body = null;
        else {
          FormatReader<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> reader = opInputReader(context);
          body = reader.readData(opInputProjection.varProjection());
        }
      } else {
        FormatReader<ReqInputVarProjection, ReqInputModelProjection<?, ?, ?>> reader = reqInputReader(context);
        body = reader.readData(inputProjection.varProjection());
      }
    } catch (FormatException | IOException e) {
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new MalformedInputInvocationError(String.format(
                  "Error reading '%s' custom request body: %s",
                  operationName,
                  e.getMessage()
              ))
          )
      );
    }

    OperationInvocation<CustomOperationRequest, ReadOperationResponse<Data>> operationInvocation =
        operationFilterChains.customOperationInvocation(operation);

    return operationInvocation.invoke(
        new CustomOperationRequest(
            requestUrl.path(),
            body,
            inputProjection,
            outputProjection.projection()
        )
    ).thenApply(result -> result.mapSuccess(success ->
        new ReadResult(
            success.getData(),
            outputProjection.pathSteps(),
            outputProjection.projection().varProjection()
        )
    ));
  }

  private @NotNull UrlCustomUrl parseCustomUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlCustomUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.CUSTOM_URL.rootElementType(),
        UrlCustomUrl.class,
        UrlSubParserDefinitions.CUSTOM_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  // ---------------------------------

  // todo extract this part into a pluggable Protocol/Marshaller ?
  // it should be able to add extra wrappers with statistics/diagnostics etc + manage error codes

  protected interface FormatResponseWriter {
    void write(@NotNull FormatWriter writer) throws IOException;
  }


  protected void writeDataResponse(
      int statusCode,
      @NotNull ReqOutputVarProjection projection,
      @Nullable Data data,
      @NotNull C context) {

    writeFormatResponse(statusCode, context, writer -> {
      writer.writeData(projection, data);
      writer.close();
    });
    closeContext(context);
  }

  protected void writeDatumResponse(
      int statusCode,
      @NotNull ReqOutputModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull C context) {

    writeFormatResponse(statusCode, context, writer -> {
      writer.writeDatum(projection, datum);
      writer.close();
    });
    closeContext(context);
  }

  protected void writeErrorResponse(@NotNull ErrorValue error, @NotNull C context) {
    writeFormatResponse(/*HttpStatusCode.OK.code(), */error.statusCode(), context, writer -> {
      writer.writeError(error);
      writer.close();
    });
    closeContext(context);
  }

  protected void writeEmptyResponse(int statusCode, @NotNull C context) {

    writeFormatResponse(statusCode, context, writer -> {
      writer.writeData(null);
      writer.close();
    });
    closeContext(context);
  }

  private void writeData(
      int statusCode,
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @Nullable Data data,
      @NotNull C context) {

    try {
      if (data == null) {
        writeEmptyResponse(statusCode, context);
      } else {
        DataPathRemover.PathRemovalResult noPathResult = DataPathRemover.removePath(reqProjection, data, pathSteps);

        if (noPathResult.error == null) {
          final @Nullable ReqOutputVarProjection varProjection = noPathResult.dataProjection;
          final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection = noPathResult.datumProjection;

          if (varProjection != null) {
            writeDataResponse(statusCode, varProjection, noPathResult.data, context);
          } else if (modelProjection != null) {
            writeDatumResponse(statusCode, modelProjection, noPathResult.datum, context);
          } else {
            writeEmptyResponse(statusCode, context);
          }
        } else {
          writeErrorResponse(noPathResult.error, context);
        }
      }

    } catch (AmbiguousPathException ignored) {
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(
//              String.format(
//                  "Can't remove %d path steps from data: \n%s\n",
//                  pathSteps == 0 ? 0 : pathSteps - 1,
//                  dataToString(data)
//              )
              String.format(
                  "Can't remove %d path steps from data",
                  pathSteps == 0 ? 0 : pathSteps - 1
              )
          ),
          context
      );
    } catch (RuntimeException e) {
      context.logger().error("Error writing response", e);
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(
              "Error writing response: " + e.getMessage()
          ),
          context
      );
    }
  }

  @SuppressWarnings("unchecked")
  private <
      O extends Operation<?, ?, ?>,
      U extends RequestUrl,
      UP extends UrlUrl,
      R extends AbstractOperationRouter<UP, ?, ?, U>,
      Rsp
      >
  CompletionStage<OperationInvocationResult<Rsp>> withOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UP urlPsi,
      @NotNull R router,
      @NotNull OperationKind operationKind,
      @NotNull Function<OperationSearchSuccess<O, U>, CompletableFuture<OperationInvocationResult<Rsp>>> continuation,
      @NotNull C context
  ) {

    try {
      OperationSearchResult<?> searchResult =
          router.findOperation(operationName, urlPsi, resource, context.typesResolver());

      if (searchResult instanceof OperationNotFound<?>) {
        return CompletableFuture.completedFuture(
            OperationInvocationResult.failure(
                new OperationNotFoundError(
                    resource.declaration().fieldName(),
                    operationKind,
                    operationName
                )
            )
        );
      }

      if (searchResult instanceof OperationSearchFailure<?>) {
        return CompletableFuture.completedFuture(
            OperationInvocationResult.failure(
                new OperationSearchFailureInvocationError(
                    resource.declaration().fieldName(),
                    requestText,
                    (OperationSearchFailure<?>) searchResult
                )
            )
        );
      }

      assert searchResult instanceof OperationSearchSuccess;
      OperationSearchSuccess<O, U> searchSuccessResult = (OperationSearchSuccess<O, U>) searchResult;
      return continuation.apply(searchSuccessResult);

    } catch (PsiProcessingException e) {
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName,
                  requestText,
                  e.errors()
              )
          )
      );
    }
  }

  private void closeContext(@NotNull C context) {
    try {
      close(context);
    } catch (IOException e) {
      context.logger().error("Error closing connections", e);
    }
  }

  private void writeGenericErrorAndClose(
      @NotNull String message,
      @NotNull HttpStatusCode status,
      @NotNull C context) {

    writeInvocationErrorAndCloseContext(
        new OperationInvocationErrorImpl(
            message, status
        ), context
    );
  }

  private void writeInvocationErrorAndCloseContext(@NotNull OperationInvocationError error, @NotNull C context) {
    writeInvocationErrorResponse(error, context);
    closeContext(context);
  }

}
