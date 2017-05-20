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
import ws.epigraph.invocation.*;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.schema.operations.*;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.*;
import ws.epigraph.service.operations.*;
import ws.epigraph.url.*;
import ws.epigraph.url.parser.CustomRequestUrlPsiParser;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.util.EBean;
import ws.epigraph.util.HttpStatusCode;

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
public abstract class AbstractHttpServer<C extends HttpInvocationContext> {
  private static final Pattern RESOURCE_PATTERN = Pattern.compile("/(\\p{Lower}\\p{Alnum}*)(.*)");

  protected final @NotNull Service service;
  private final @NotNull ServerProtocol<C> serverProtocol;
  private final @NotNull OperationFilterChains<Data> operationFilterChains;

  @SuppressWarnings("unchecked")
  protected AbstractHttpServer(
      final @NotNull Service service,
      final @NotNull ServerProtocol<C> serverProtocol,
      final @NotNull OperationFilterChains<? extends Data> invocations) {
    this.service = service;
    this.serverProtocol = serverProtocol;
    operationFilterChains = (OperationFilterChains<Data>) invocations;
  }

  protected abstract long responseTimeout(@NotNull C context);

  protected void close(@NotNull C context) throws IOException { }

  // -----------------------------------

  protected @NotNull OperationInvocationContext newOperationInvocationContext(@NotNull C context) {
    return new DefaultOperationInvocationContext(context.isDebug(), new EBean());
  }

  protected void handleRequest(
      @NotNull String decodedUri,
      @NotNull HttpMethod requestMethod,
      @Nullable String operationName,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {


    try {
      // extract resource name from URI
      Matcher matcher = RESOURCE_PATTERN.matcher(decodedUri);
      if (!matcher.matches()) {
        writeGenericErrorAndClose(
            String.format(
                "Bad URL format. Supported resources: {%s}",
                Util.listSupportedResources(service)
            ),
            HttpStatusCode.BAD_REQUEST,
            context,
            operationInvocationContext
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
            ),
            HttpStatusCode.BAD_REQUEST,
            context,
            operationInvocationContext
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
              context,
              operationInvocationContext
          );
          return;
        }
      }

      if (requestMethod == HttpMethod.GET)
        handleReadRequest(resource, operationName, decodedUri, context, operationInvocationContext);
      else if (requestMethod == HttpMethod.POST)
        handleCreateRequest(resource, operationName, decodedUri, context, operationInvocationContext);
      else if (requestMethod == HttpMethod.PUT)
        handleUpdateRequest(resource, operationName, decodedUri, context, operationInvocationContext);
      else if (requestMethod == HttpMethod.DELETE)
        handleDeleteRequest(resource, operationName, decodedUri, context, operationInvocationContext);
      else {
        writeGenericErrorAndClose(
            String.format("Unsupported HTTP method '%s'", requestMethod),
            HttpStatusCode.BAD_REQUEST,
            context,
            operationInvocationContext
        );
      }
    } catch (RuntimeException e) {
      writeGenericErrorAndClose(
          String.format("Internal error '%s'", e),
          HttpStatusCode.INTERNAL_SERVER_ERROR,
          context,
          operationInvocationContext
      );
      context.logger().error("Unexpected exception", e);
    }

  }

  // ----------------------------------- READ

  private void handleReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    handleReadResponse(
        OperationKind.READ,
        invokeReadRequest(resource, operationName, decodedUri, context, operationInvocationContext),
        context,
        operationInvocationContext
    );
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
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null &&
        !operationName.equals(ReadOperationDeclaration.DEFAULT_NAME) &&
        resource.namedReadOperation(operationName) == null)
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

          OperationInvocation<ReadOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterRead(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new ReadOperationRequest(
                  requestUrl.path(),
                  outputProjection.projection()
              ), operationInvocationContext
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

    if (context.isDebug())
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
      @NotNull OperationKind operationKind,
      @NotNull CompletionStage<OperationInvocationResult<ReadResult>> responseFuture,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    final Consumer<OperationInvocationResult<ReadResult>> resultConsumer =
        invocationResult ->
            invocationResult.consume(

                readResult -> {
                  try {
                    writeData(
                        operationKind,
                        readResult.pathSteps,
                        readResult.projection,
                        readResult.data,
                        context,
                        operationInvocationContext
                    );
                  } catch (RuntimeException e) {
                    writeInvocationErrorAndCloseContext(
                        new GenericServerInvocationError(e.toString()),
                        context,
                        operationInvocationContext
                    );
                  }
                },

                error -> writeInvocationErrorAndCloseContext(error, context, operationInvocationContext)
            );

    final Function<Throwable, Void> failureConsumer = throwable -> {
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(throwable.getMessage()),
          context,
          operationInvocationContext
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

  private void handleCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {
    handleReadResponse(
        OperationKind.CREATE,
        invokeCreateRequest(resource, operationName, decodedUri, context, operationInvocationContext),
        context,
        operationInvocationContext
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null &&
        !operationName.equals(CreateOperationDeclaration.DEFAULT_NAME) &&
        resource.namedCreateOperation(operationName) == null)
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
            body = serverProtocol.readInput(
                operation.declaration().inputProjection().varProjection(),
                inputProjection == null ? null : inputProjection.varProjection(),
                context,
                operationInvocationContext
            );
          } catch (IOException e) {
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(
                    new MalformedInputInvocationError(String.format(
                        "Error reading %screate request body: %s",
                        operationName == null ||
                        CreateOperationDeclaration.DEFAULT_NAME.equals(operationName) ? "" : "'" + operationName + "' ",
                        e.getMessage()
                    ))
                )
            );
          }

          if (body == null)
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(new MalformedInputInvocationError("Null body for create operation"))
            );

          OperationInvocation<CreateOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterCreate(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new CreateOperationRequest(
                  requestUrl.path(),
                  body,
                  inputProjection,
                  outputProjection.projection()
              ), operationInvocationContext
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

    if (context.isDebug())
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

  private void handleUpdateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {
    handleReadResponse(
        OperationKind.UPDATE,
        invokeUpdateRequest(resource, operationName, decodedUri, context, operationInvocationContext),
        context,
        operationInvocationContext
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeUpdateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null &&
        !operationName.equals(UpdateOperationDeclaration.DEFAULT_NAME) &&
        resource.namedUpdateOperation(operationName) == null)
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
            body = serverProtocol.readUpdateInput(
                operation.declaration().inputProjection().varProjection(),
                updateProjection == null ? null : updateProjection.varProjection(),
                context,
                operationInvocationContext
            );
          } catch (IOException e) {
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(
                    new MalformedInputInvocationError(String.format(
                        "Error reading %supdate request body: %s",
                        operationName == null ||
                        UpdateOperationDeclaration.DEFAULT_NAME.equals(operationName) ? "" : "'" + operationName + "' ",
                        e.getMessage()
                    ))
                )
            );
          }

          if (body == null)
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(new MalformedInputInvocationError("Null body for update operation"))
            );

          OperationInvocation<UpdateOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterUpdate(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new UpdateOperationRequest(
                  requestUrl.path(),
                  body,
                  updateProjection,
                  outputProjection.projection()
              ), operationInvocationContext
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

    if (context.isDebug())
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

  private void handleDeleteRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {
    handleReadResponse(
        OperationKind.DELETE,
        invokeDeleteRequest(resource, operationName, decodedUri, context, operationInvocationContext),
        context,
        operationInvocationContext
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeDeleteRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null &&
        !operationName.equals(DeleteOperationDeclaration.DEFAULT_NAME) &&
        resource.namedDeleteOperation(operationName) == null)
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

          OperationInvocation<DeleteOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterDelete(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new DeleteOperationRequest(
                  requestUrl.path(),
                  deleteProjection,
                  outputProjection.projection()
              ), operationInvocationContext
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

    if (context.isDebug())
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

  private void handleCustomRequest(
      @NotNull Resource resource,
      @NotNull String operationName,
      @NotNull HttpMethod method,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {
    handleReadResponse(
        OperationKind.CUSTOM,
        invokeCustomRequest(resource, operationName, method, decodedUri, context, operationInvocationContext),
        context,
        operationInvocationContext
    );
  }

  @SuppressWarnings("unchecked")
  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeCustomRequest(
      @NotNull Resource resource,
      @NotNull String operationName,
      @NotNull HttpMethod method,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

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

    final Data body;
    try {
      final OpInputFieldProjection opInputProjection = operation.declaration().inputProjection();
      body = opInputProjection == null
             ? null
             : serverProtocol.readInput(
                 opInputProjection.varProjection(),
                 inputProjection == null ? null : inputProjection.varProjection(),
                 context,
                 operationInvocationContext
             );
    } catch (IOException e) {
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

    OperationInvocation<CustomOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
        operationFilterChains.filterCustom(new LocalOperationInvocation<>(operation));

    return operationInvocation.invoke(
        new CustomOperationRequest(
            requestUrl.path(),
            body,
            inputProjection,
            outputProjection.projection()
        ), operationInvocationContext
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

    if (context.isDebug())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  // ---------------------------------

  private void writeData(
      final @NotNull OperationKind operationKind,
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @Nullable Data data,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    try {
      if (data == null) {
        serverProtocol.writeEmptyResponse(operationKind, context, operationInvocationContext);
      } else {
        DataPathRemover.PathRemovalResult noPathResult = DataPathRemover.removePath(reqProjection, data, pathSteps);

        if (noPathResult.error == null) {
          final @Nullable ReqOutputVarProjection varProjection = noPathResult.dataProjection;
          final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection = noPathResult.datumProjection;

          if (varProjection != null) {
            serverProtocol.writeDataResponse(
                operationKind,
                varProjection,
                noPathResult.data,
                context,
                operationInvocationContext
            );
          } else if (modelProjection != null) {
            serverProtocol.writeDatumResponse(
                operationKind,
                modelProjection,
                noPathResult.datum,
                context,
                operationInvocationContext
            );
          } else {
            serverProtocol.writeEmptyResponse(operationKind, context, operationInvocationContext);
          }
        } else {
          serverProtocol.writeErrorResponse(
              operationKind,
              noPathResult.error,
              context,
              operationInvocationContext
          );
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
          context,
          operationInvocationContext
      );
    } catch (RuntimeException e) {
      context.logger().error("Error writing response", e);
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(
              "Error writing response: " + e.getMessage()
          ),
          context,
          operationInvocationContext
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
      @NotNull C context) {

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
      int status,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeInvocationErrorAndCloseContext(
        new OperationInvocationErrorImpl(status, message),
        context,
        operationInvocationContext
    );
  }

  protected void writeInvocationErrorAndCloseContext(
      @NotNull OperationInvocationError error,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    serverProtocol.writeInvocationErrorResponse(error, context, operationInvocationContext);
    closeContext(context);
    // operationInvocationContext.close();
  }

}
