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
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.*;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.schema.operations.*;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ResourceNotFoundException;
import ws.epigraph.service.ResourceRouter;
import ws.epigraph.service.Service;
import ws.epigraph.service.operations.*;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.CustomRequestUrlPsiParser;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlUrl;
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
  private static final Pattern RESOURCE_PATTERN = Pattern.compile("/\\+?(\\p{Lower}\\p{Alnum}*)(.*)");

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

      switch (requestMethod) {
        case GET:
          handleReadRequest(resource, operationName, decodedUri, context, operationInvocationContext);
          break;
        case POST:
          handleCreateRequest(resource, operationName, decodedUri, context, operationInvocationContext);
          break;
        case PUT:
          handleUpdateRequest(resource, operationName, decodedUri, context, operationInvocationContext);
          break;
        case DELETE:
          handleDeleteRequest(resource, operationName, decodedUri, context, operationInvocationContext);
          break;
        default:
          writeGenericErrorAndClose(
              String.format("Unsupported HTTP method '%s'", requestMethod),
              HttpStatusCode.BAD_REQUEST,
              context,
              operationInvocationContext
          );
          break;
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
    final @NotNull ReqProjection<?,?> projection;

    ReadResult(
        final @Nullable Data data,
        final int steps,
        final @NotNull ReqProjection<?,?> projection) {
      this.data = data;
      pathSteps = steps;
      this.projection = projection;
    }
  }

  @SuppressWarnings("unchecked")
  private @NotNull CompletionStage<InvocationResult<ReadResult>> invokeReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null &&
        !ReadOperationDeclaration.DEFAULT_NAME.equals(operationName) &&
        resource.namedReadOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
              new NotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUrl urlPsi = parseNonReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
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
          RequestUrl requestUrl = operationSearchResult.requestUrl();
          StepsAndProjection<ReqFieldProjection> outputProjection = requestUrl.outputProjection();

          ReadOperation<Data> operation = operationSearchResult.operation();

          OperationInvocation<ReadOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterRead(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new ReadOperationRequest(
                  requestUrl.path(),
                  outputProjection
              ), operationInvocationContext
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success == null ? null : success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().projection()
              )
          ));
        },
        context
    );
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<InvocationResult<R>> withReadOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<ReadOperation<Data>>, CompletableFuture<InvocationResult<R>>> continuation,
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
      @NotNull CompletionStage<InvocationResult<ReadResult>> responseFuture,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    final Consumer<InvocationResult<ReadResult>> resultConsumer =
        invocationResult ->
            invocationResult.consume(

                readResult -> {
                  try {
                    if (readResult == null)
                      serverProtocol.writeEmptyResponse(operationKind, context, operationInvocationContext);
                    else
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
      if (context.isDebug())
        context.logger().error("Unhandled exception", throwable);
      writeInvocationErrorAndCloseContext(
          new GenericServerInvocationError(throwable.getMessage()),
          context,
          operationInvocationContext
      );
      return null;
    };

    long timeout = responseTimeout(context);
    if (timeout > 0) {
      CompletionStage<InvocationResult<ReadResult>> timeoutStage = Util.failAfter(Duration.ofMillis(timeout));
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

  private @NotNull CompletionStage<InvocationResult<ReadResult>> invokeCreateRequest(
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
          InvocationResult.failure(
              new NotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.CREATE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUrl urlPsi = parseNonReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
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
          @NotNull RequestUrl requestUrl = operationSearchResult.requestUrl();
          @Nullable StepsAndProjection<ReqFieldProjection> reqInputProjection = requestUrl.inputProjection();
          StepsAndProjection<ReqFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull CreateOperation<Data> operation = operationSearchResult.operation();

          final Data body;
          try {
            body = serverProtocol.readInput(
                operation.declaration().inputProjection().projection(),
                StepsAndProjection.unwrapNullable(reqInputProjection, AbstractFieldProjection::projection),
                context,
                operationInvocationContext
            );
          } catch (IOException e) {
            return CompletableFuture.completedFuture(
                InvocationResult.failure(
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
                InvocationResult.failure(new MalformedInputInvocationError("Null body for create operation"))
            );

          OperationInvocation<CreateOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterCreate(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new CreateOperationRequest(
                  requestUrl.path(),
                  body,
                  reqInputProjection,
                  outputProjection
              ), operationInvocationContext
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success == null ? null : success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().projection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlUrl parseNonReadUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.URL.rootElementType(),
        UrlUrl.class,
        UrlSubParserDefinitions.URL,
        errorsAccumulator
    );

    if (context.isDebug())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<InvocationResult<R>> withCreateOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<CreateOperation<Data>>, CompletableFuture<InvocationResult<R>>> continuation,
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

  private @NotNull CompletionStage<InvocationResult<ReadResult>> invokeUpdateRequest(
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
          InvocationResult.failure(
              new NotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.UPDATE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUrl urlPsi = parseNonReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
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
          @NotNull RequestUrl requestUrl = operationSearchResult.requestUrl();
          @Nullable StepsAndProjection<ReqFieldProjection> updateStepsAndProjection = requestUrl.inputProjection();

          StepsAndProjection<ReqFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull UpdateOperation<Data> operation = operationSearchResult.operation();

          final Data body;
          try {
            body = serverProtocol.readInput(
                operation.declaration().inputProjection().projection(),
                StepsAndProjection.unwrapNullable(updateStepsAndProjection, AbstractFieldProjection::projection),
                context,
                operationInvocationContext
            );
          } catch (IOException e) {
            return CompletableFuture.completedFuture(
                InvocationResult.failure(
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
                InvocationResult.failure(new MalformedInputInvocationError("Null body for update operation"))
            );

          OperationInvocation<UpdateOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterUpdate(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new UpdateOperationRequest(
                  requestUrl.path(),
                  body,
                  updateStepsAndProjection,
                  outputProjection
              ), operationInvocationContext
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success == null ? null : success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().projection()
              )
          ));
        },
        context
    );
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<InvocationResult<R>> withUpdateOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<UpdateOperation<Data>>, CompletableFuture<InvocationResult<R>>> continuation,
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

  private @NotNull CompletionStage<InvocationResult<ReadResult>> invokeDeleteRequest(
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
          InvocationResult.failure(
              new NotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.DELETE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUrl urlPsi = parseNonReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
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
          @NotNull RequestUrl requestUrl = operationSearchResult.requestUrl();
          @Nullable StepsAndProjection<ReqFieldProjection> deleteStepsAndProjection = requestUrl.inputProjection();
          assert deleteStepsAndProjection != null; // ensured by DeleteOperationRouter
          @NotNull ReqFieldProjection deleteProjection = deleteStepsAndProjection.projection();

          StepsAndProjection<ReqFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull DeleteOperation<Data> operation = operationSearchResult.operation();

          OperationInvocation<DeleteOperationRequest, ReadOperationResponse<Data>, ?> operationInvocation =
              operationFilterChains.filterDelete(new LocalOperationInvocation<>(operation));

          return operationInvocation.invoke(
              new DeleteOperationRequest(
                  requestUrl.path(),
                  deleteProjection,
                  outputProjection
              ), operationInvocationContext
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success == null ? null : success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().projection()
              )
          ));
        },
        context
    );
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<InvocationResult<R>> withDeleteOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<DeleteOperation<Data>>, CompletableFuture<InvocationResult<R>>> continuation,
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
  private @NotNull CompletionStage<InvocationResult<ReadResult>> invokeCustomRequest(
      @NotNull Resource resource,
      @NotNull String operationName,
      @NotNull HttpMethod method,
      @NotNull String decodedUri,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    final CustomOperation<Data> operation = (CustomOperation<Data>) resource.customOperation(method, operationName);
    if (operation == null)
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
              new NotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  method,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlUrl urlPsi = parseNonReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    RequestUrl requestUrl = null;
    PsiProcessingContext psiProcessingContext = new DefaultPsiProcessingContext();
    try {
      requestUrl = new CustomRequestUrlPsiParser(psiProcessingContext).parseRequestUrl(
          resource.declaration().fieldType(),
          operation.declaration(),
          urlPsi,
          context.typesResolver(),
          psiProcessingContext
      );
    } catch (PsiProcessingException e) {
      psiProcessingContext.setMessages(e.messages());
    }

    if (!psiProcessingContext.messages().isEmpty()) {
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CUSTOM,
                  operationName,
                  decodedUri,
                  psiProcessingContext.messages()
              )
          )
      );
    }

    assert requestUrl != null;

    @Nullable StepsAndProjection<ReqFieldProjection> reqInputProjection = requestUrl.inputProjection();
    StepsAndProjection<ReqFieldProjection> outputProjection = requestUrl.outputProjection();

    final Data body;
    try {
      final @Nullable OpFieldProjection opInputProjection = operation.declaration().inputProjection();
      body = opInputProjection == null
             ? null
             : serverProtocol.readInput(
                 opInputProjection.projection(),
                 StepsAndProjection.unwrapNullable(reqInputProjection, AbstractFieldProjection::projection),
                 context,
                 operationInvocationContext
             );
    } catch (IOException e) {
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
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
            reqInputProjection,
            outputProjection
        ), operationInvocationContext
    ).thenApply(result -> result.mapSuccess(success ->
        new ReadResult(
            success == null ? null : success.getData(),
            outputProjection.pathSteps(),
            outputProjection.projection().projection()
        )
    ));
  }

  // ---------------------------------

  private void writeData(
      final @NotNull OperationKind operationKind,
      final int pathSteps,
      @NotNull ReqProjection<?,?> reqProjection,
      @Nullable Data data,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    try {
      if (data == null) {
        serverProtocol.writeEmptyResponse(operationKind, context, operationInvocationContext);
      } else {

        // any reasonable way to get rid of this extra traversal? should be a big performance hit though
        ErrorValue error = DataErrorLocator.getError(reqProjection, pathSteps, data);
        if (error == null) {
          serverProtocol.writeDataResponse(
              operationKind,
              new StepsAndProjection<>(pathSteps, reqProjection.normalizedForType(data.type())),
              data,
              context,
              operationInvocationContext
          );
        } else {
          serverProtocol.writeErrorResponse(
              operationKind,
              error,
              context,
              operationInvocationContext
          );
        }

      }

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
      UP extends UrlUrl,
      R extends AbstractOperationRouter<UP, ?, ?>,
      Rsp
      >
  CompletionStage<InvocationResult<Rsp>> withOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UP urlPsi,
      @NotNull R router,
      @NotNull OperationKind operationKind,
      @NotNull Function<OperationSearchSuccess<O>, CompletableFuture<InvocationResult<Rsp>>> continuation,
      @NotNull C context) {

    try {
      OperationSearchResult<?> searchResult =
          router.findOperation(operationName, urlPsi, resource, context.typesResolver());

      if (searchResult instanceof OperationNotFound<?>) {
        return CompletableFuture.completedFuture(
            InvocationResult.failure(
                new NotFoundError(
                    resource.declaration().fieldName(),
                    operationKind,
                    operationName
                )
            )
        );
      }

      if (searchResult instanceof OperationSearchFailure<?>) {
        return CompletableFuture.completedFuture(
            InvocationResult.failure(
                new SearchFailureInvocationError(
                    resource.declaration().fieldName(),
                    requestText,
                    (OperationSearchFailure<?>) searchResult
                )
            )
        );
      }

      assert searchResult instanceof OperationSearchSuccess;
      OperationSearchSuccess<O> searchSuccessResult = (OperationSearchSuccess<O>) searchResult;
      return continuation.apply(searchSuccessResult);

    } catch (PsiProcessingException e) {
      return CompletableFuture.completedFuture(
          InvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName,
                  requestText,
                  e.messages()
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
        new InvocationErrorImpl(status, message),
        context,
        operationInvocationContext
    );
  }

  protected void writeInvocationErrorAndCloseContext(
      @NotNull InvocationError error,
      @NotNull C context,
      @NotNull OperationInvocationContext operationInvocationContext) {

    serverProtocol.writeInvocationErrorResponse(error, context, operationInvocationContext);
    closeContext(context);
    // operationInvocationContext.close();
  }

}
