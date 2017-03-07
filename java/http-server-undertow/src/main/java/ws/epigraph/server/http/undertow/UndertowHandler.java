/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.server.http.undertow;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
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
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.server.http.RequestHeaders;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.*;
import ws.epigraph.service.operations.*;
import ws.epigraph.url.*;
import ws.epigraph.url.parser.CustomRequestUrlPsiParser;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.wire.json.reader.OpInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ws.epigraph.server.http.undertow.Constants.CONTENT_TYPE_JSON;
import static ws.epigraph.server.http.undertow.Constants.CONTENT_TYPE_TEXT;
import static ws.epigraph.server.http.undertow.Util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHandler implements HttpHandler {
  private static final Logger LOG = LoggerFactory.getLogger(UndertowHandler.class); // assuming a thread-safe backend
  private static final Pattern RESOURCE_PATTERN = Pattern.compile("/(\\p{Lower}\\p{Alnum}*)(.*)");
  private final @NotNull Service service;
  private final @NotNull TypesResolver typesResolver;
  private final long responseTimeout;

  public UndertowHandler(@NotNull Service service, @NotNull TypesResolver typesResolver, long responseTimeout) {
    this.service = service;
    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) {
    // dispatch to a worker thread so we can go to blocking mode and enable streaming
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    exchange.startBlocking();

    final Sender sender = exchange.getResponseSender();

    try {
      String decodedUri = getDecodedRequestString(exchange);
      String resourceName = getResourceName(decodedUri, exchange);

      Resource resource = ResourceRouter.findResource(resourceName, service);
      String operationName = getOperationName(exchange);

      @NotNull HttpMethod requestMethod = getMethod(exchange);

      if (operationName != null) {
        final CustomOperation<?> customOperation = resource.customOperation(requestMethod, operationName);
        if (customOperation != null) {
          UrlCustomUrl urlPsi = parseCustomUrlPsi(decodedUri, exchange);
          handleCustomRequest(resource, urlPsi, customOperation, exchange);
          return;
        }
      }

      if (requestMethod == HttpMethod.GET) {
        UrlReadUrl urlPsi = parseReadUrlPsi(decodedUri, exchange);
        handleReadRequest(resource, operationName, urlPsi, exchange);
      } else if (requestMethod == HttpMethod.POST) {
        UrlCreateUrl urlPsi = parseCreateUrlPsi(decodedUri, exchange);
        handleCreateRequest(resource, operationName, urlPsi, exchange);
      } else if (requestMethod == HttpMethod.PUT) {
        UrlUpdateUrl urlPsi = parseUpdateUrlPsi(decodedUri, exchange);
        handleUpdateRequest(resource, operationName, urlPsi, exchange);
      } else if (requestMethod == HttpMethod.DELETE) {
        UrlDeleteUrl urlPsi = parseDeleteUrlPsi(decodedUri, exchange);
        handleDeleteRequest(resource, operationName, urlPsi, exchange);
      } else {
        badRequest("Unsupported HTTP method '" + requestMethod + "'\n", CONTENT_TYPE_TEXT, exchange);
        //noinspection ThrowCaughtLocally
        throw RequestFailedException.INSTANCE;
      }

    } catch (ResourceNotFoundException e) {
      badRequest(e.getMessage() + ". Supported resources: {" + listSupportedResources(service) + "}",
          CONTENT_TYPE_TEXT, exchange
      );
    } catch (OperationNotFoundException e) {
      badRequest(e.getMessage(), CONTENT_TYPE_TEXT, exchange);
    } catch (RequestFailedException ignored) { // already handled
    } catch (Exception e) {
      LOG.error("Internal exception", e);
      exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
      sender.send(e.getMessage());
    } finally {
      sender.close();
    }
  }

  @Contract(pure = true)
  private @NotNull HttpMethod getMethod(final @NotNull HttpServerExchange exchange) throws RequestFailedException {
    final HttpString methodString = exchange.getRequestMethod();
    if (methodString.equals(Methods.GET)) return HttpMethod.GET;
    if (methodString.equals(Methods.PUT)) return HttpMethod.PUT;
    if (methodString.equals(Methods.POST)) return HttpMethod.POST;
    if (methodString.equals(Methods.DELETE)) return HttpMethod.DELETE;

    badRequest("Unsupported HTTP method '" + methodString + "'\n", CONTENT_TYPE_TEXT, exchange);
    //noinspection ThrowCaughtLocally
    throw RequestFailedException.INSTANCE;
  }

  private @NotNull String getResourceName(
      final @NotNull String url,
      final @NotNull HttpServerExchange exchange) throws RequestFailedException {

    Matcher matcher = RESOURCE_PATTERN.matcher(url);

    if (!matcher.matches()) {
      badRequest(
          String.format(
              "Bad URL format. Supported resources: {%s}\n",
              Util.listSupportedResources(service)
          )
          , CONTENT_TYPE_TEXT, exchange);
      throw RequestFailedException.INSTANCE;
    }

    return matcher.group(1);
  }

  private @Nullable String getOperationName(@NotNull HttpServerExchange exchange) {
    final HeaderValues headerValues = exchange.getRequestHeaders().get(RequestHeaders.OPERATION_NAME);
    return headerValues == null ? null : headerValues.getFirst(); // warn if more than one?
  }

  @SuppressWarnings("unchecked")
  private <
      O extends Operation<?, ?, ?>,
      U extends RequestUrl,
      UP extends UrlUrl,
      R extends AbstractOperationRouter<UP, ?, ?, U>
      >
  OperationSearchSuccess<O, U> findOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UP urlPsi,
      final @NotNull R router,
      final @NotNull OperationKind operationKind,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    final OperationSearchResult<?> searchResult =
        router.findOperation(operationName, urlPsi, resource, typesResolver);

    if (searchResult instanceof OperationNotFound<?>)
      throw new OperationNotFoundException(resource.declaration().fieldName(), operationKind, operationName);

    if (searchResult instanceof OperationSearchFailure<?>) {
      Util.reportOperationSearchFailureAndFail(urlPsi.getText(), (OperationSearchFailure<?>) searchResult, exchange);
    }

    assert searchResult instanceof OperationSearchSuccess;
    return (OperationSearchSuccess<O, U>) searchResult;
  }

  private void writeDataResponse(
      int statusCode,
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @Nullable Data data,
      @NotNull HttpServerExchange exchange) {

    // todo validate response: e.g. all required parts must be present

    String contentType = CONTENT_TYPE_JSON; // todo should depend on marshaller
    String responseText = null;

    Data trimmedData = data;

    try {
      // FIXME change path removers so there's no need for pre-marshalling trimming
      trimmedData = data == null ? null : ProjectionDataTrimmer.trimData(data, reqProjection);

      if (trimmedData == null) {
        responseText = getNullResponse();
      } else {
        DataPathRemover.PathRemovalResult noPathData = DataPathRemover.removePath(trimmedData, pathSteps);

        if (noPathData.error == null) {
          final OutputProjectionPathRemover.PathRemovalResult noPathProjection =
              OutputProjectionPathRemover.removePath(reqProjection, pathSteps);

          final @Nullable ReqOutputVarProjection varProjection = noPathProjection.varProjection();
          final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection = noPathProjection.modelProjection();

          if (varProjection != null) {
            exchange.setStatusCode(statusCode);
            writeData(varProjection, noPathData.data, exchange.getOutputStream());
          } else if (modelProjection != null) {
            exchange.setStatusCode(statusCode);
            writeDatum(modelProjection, noPathData.datum, exchange.getOutputStream());
          } else {
            responseText = getNullResponse();
          }
        } else {
          contentType = CONTENT_TYPE_TEXT; // todo report errors in json too?
          statusCode = noPathData.error.statusCode();
          responseText = noPathData.error.message();

          final @Nullable Exception cause = noPathData.error.cause;
          if (cause != null) {
            responseText = responseText + "\ncaused by: " + cause.toString();
            //add stacktrace too?
          }
        }

      }

      if (responseText != null)
        writeResponse(statusCode, responseText + "\n", contentType, exchange);

    } catch (AmbiguousPathException ignored) {
      serverError(
          String.format(
              "Can't remove %d path steps from data: \n%s\n",
              pathSteps == 0 ? 0 : pathSteps - 1,
              dataToString(trimmedData)
          ),
          CONTENT_TYPE_TEXT,
          exchange
      );
    } catch (Exception e) {
      LOG.error("Error writing response", e);
      final String message = e.getMessage();
      serverError(message == null ? null : message + "\n", CONTENT_TYPE_TEXT, exchange);
    } finally {
      exchange.getResponseSender().close();
    }
  }

  // read --------------------------------------------------------------------------------------------------------------

  private UrlReadUrl parseReadUrlPsi(
      @NotNull String urlString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.READ_URL.rootElementType(),
        UrlReadUrl.class,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors())
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<ReadOperation<?>, ReadRequestUrl> findReadOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    return findOperation(resource, operationName, urlPsi, ReadOperationRouter.INSTANCE, OperationKind.READ, exchange);
  }

  private void handleReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlReadUrl urlPsi,
      @NotNull HttpServerExchange exchange) throws OperationNotFoundException, RequestFailedException {

    try {
      // find operation
      OperationSearchSuccess<ReadOperation<?>, ReadRequestUrl> operationSearchResult = findReadOperation(
          resource,
          operationName,
          urlPsi,
          exchange
      );

      final @NotNull ReadRequestUrl readRequestUrl = operationSearchResult.requestUrl();
      final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection = readRequestUrl.outputProjection();

      // run operation
      CompletableFuture<? extends ReadOperationResponse<?>> future = operationSearchResult.operation().process(
          new ReadOperationRequest(
              readRequestUrl.path(),
              outputProjection.projection()
          ));

      // send response back
      handleReadResponse(
          StatusCodes.OK,
          outputProjection.pathSteps(),
          outputProjection.projection().varProjection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
  }

  private <R extends ReadOperationResponse<?>> void handleReadResponse(
      final int statusCode,
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull CompletionStage<R> responseFuture,
      final @NotNull HttpServerExchange exchange
  ) {

    final Consumer<R> resultConsumer = readOperationResponse -> {
      Sender sender = exchange.getResponseSender();

      try {
        @Nullable Data data = readOperationResponse.getData();
        writeDataResponse(statusCode, pathSteps, reqProjection, data, exchange);
      } catch (Exception e) {
        LOG.error("Error processing request", e);
        serverError(e.getMessage(), CONTENT_TYPE_TEXT, exchange);
      } finally {
        sender.close();
      }

    };

    final Function<Throwable, Void> failureConsumer = throwable -> {
      serverError(throwable.getMessage(), CONTENT_TYPE_TEXT, exchange);
      return null;
    };

    if (responseTimeout > 0) {
      CompletionStage<R> timeout = failAfter(Duration.ofMillis(responseTimeout));
      responseFuture.acceptEither(timeout, resultConsumer).exceptionally(failureConsumer);
    } else {
      responseFuture.thenAccept(resultConsumer).exceptionally(failureConsumer);
    }
  }

  // create ------------------------------------------------------------------------------------------------------------

  private UrlCreateUrl parseCreateUrlPsi(
      @NotNull String urlString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlCreateUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.CREATE_URL.rootElementType(),
        UrlCreateUrl.class,
        UrlSubParserDefinitions.CREATE_URL,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors())
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<CreateOperation<?>, CreateRequestUrl> findCreateOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlCreateUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    return findOperation(
        resource,
        operationName,
        urlPsi,
        CreateOperationRouter.INSTANCE,
        OperationKind.CREATE,
        exchange
    );
  }

  private void handleCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlCreateUrl urlPsi,
      @NotNull HttpServerExchange exchange) throws OperationNotFoundException, RequestFailedException, IOException {

    try {
      // find operation
      OperationSearchSuccess<CreateOperation<?>, CreateRequestUrl> operationSearchResult = findCreateOperation(
          resource,
          operationName,
          urlPsi,
          exchange
      );

      final @NotNull CreateOperation<?> operation = operationSearchResult.operation();
      final @NotNull CreateRequestUrl createRequestUrl = operationSearchResult.requestUrl();
      final @Nullable ReqInputFieldProjection inputProjection = createRequestUrl.inputProjection();

      // read body
      final Data body;
      JsonParser bodyParser = new JsonFactory().createParser(exchange.getInputStream());
      if (inputProjection == null) {
        OpInputJsonFormatReader bodyReader = new OpInputJsonFormatReader(bodyParser);
        body = wrapIAE(exchange, "Error reading request body", () ->
            bodyReader.readData(operation.declaration().inputProjection().varProjection())
        );
      } else {
        ReqInputJsonFormatReader bodyReader = new ReqInputJsonFormatReader(bodyParser);
        body = wrapIAE(exchange, "Error reading request body", () ->
            bodyReader.readData(inputProjection.varProjection())
        );
      }

      // run operation
      final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection =
          createRequestUrl.outputProjection();

      CompletionStage<? extends ReadOperationResponse<?>> future = operation.process(
          new CreateOperationRequest(
              createRequestUrl.path(),
              body,
              inputProjection,
              outputProjection.projection()
          ));

      // send response back
      handleReadResponse(
          StatusCodes.CREATED,
          outputProjection.pathSteps(),
          outputProjection.projection().varProjection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
  }

  // update ------------------------------------------------------------------------------------------------------------

  private UrlUpdateUrl parseUpdateUrlPsi(
      @NotNull String urlString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlUpdateUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.UPDATE_URL.rootElementType(),
        UrlUpdateUrl.class,
        UrlSubParserDefinitions.UPDATE_URL,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors())
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<UpdateOperation<?>, UpdateRequestUrl> findUpdateOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlUpdateUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    return findOperation(
        resource,
        operationName,
        urlPsi,
        UpdateOperationRouter.INSTANCE,
        OperationKind.UPDATE,
        exchange
    );
  }

  private void handleUpdateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlUpdateUrl urlPsi,
      @NotNull HttpServerExchange exchange) throws OperationNotFoundException, RequestFailedException, IOException {

    try {
      // find operation
      OperationSearchSuccess<UpdateOperation<?>, UpdateRequestUrl> operationSearchResult = findUpdateOperation(
          resource,
          operationName,
          urlPsi,
          exchange
      );

      final @NotNull UpdateOperation<?> operation = operationSearchResult.operation();
      final @NotNull UpdateRequestUrl updateRequestUrl = operationSearchResult.requestUrl();
      final @Nullable ReqUpdateFieldProjection updateProjection = updateRequestUrl.updateProjection();

      if (updateProjection == null) {
        badRequest("Update projection must be specified", CONTENT_TYPE_TEXT, exchange);
        throw RequestFailedException.INSTANCE;
      }

      // read body
      final JsonParser bodyParser = new JsonFactory().createParser(exchange.getInputStream());
      final ReqUpdateJsonFormatReader bodyReader = new ReqUpdateJsonFormatReader(bodyParser);
      final Data body = wrapIAE(exchange, "Error reading request body", () ->
          bodyReader.readData(updateProjection.varProjection())
      );

      // run operation
      final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection =
          updateRequestUrl.outputProjection();

      CompletionStage<? extends ReadOperationResponse<?>> future = operation.process(
          new UpdateOperationRequest(
              updateRequestUrl.path(),
              body,
              updateProjection,
              outputProjection.projection()
          ));

      // send response back
      handleReadResponse(
          StatusCodes.OK,
          outputProjection.pathSteps(),
          outputProjection.projection().varProjection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
  }

  // delete ------------------------------------------------------------------------------------------------------------

  private UrlDeleteUrl parseDeleteUrlPsi(
      @NotNull String urlString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlDeleteUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.DELETE_URL.rootElementType(),
        UrlDeleteUrl.class,
        UrlSubParserDefinitions.DELETE_URL,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors())
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<DeleteOperation<?>, DeleteRequestUrl> findDeleteOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlDeleteUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    return findOperation(
        resource,
        operationName,
        urlPsi,
        DeleteOperationRouter.INSTANCE,
        OperationKind.DELETE,
        exchange
    );
  }

  private void handleDeleteRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlDeleteUrl urlPsi,
      @NotNull HttpServerExchange exchange) throws OperationNotFoundException, RequestFailedException {

    try {
      // find operation
      OperationSearchSuccess<DeleteOperation<?>, DeleteRequestUrl> operationSearchResult = findDeleteOperation(
          resource,
          operationName,
          urlPsi,
          exchange
      );

      final @NotNull DeleteOperation<?> operation = operationSearchResult.operation();
      final @NotNull DeleteRequestUrl deleteRequestUrl = operationSearchResult.requestUrl();
      final @NotNull ReqDeleteFieldProjection deleteProjection = deleteRequestUrl.deleteProjection();

      // run operation
      final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection =
          deleteRequestUrl.outputProjection();

      CompletionStage<? extends ReadOperationResponse<?>> future = operation.process(
          new DeleteOperationRequest(
              deleteRequestUrl.path(),
              deleteProjection,
              outputProjection.projection()
          ));

      // send response back
      handleReadResponse(
          StatusCodes.OK,
          outputProjection.pathSteps(),
          outputProjection.projection().varProjection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
  }

  // custom ------------------------------------------------------------------------------------------------------------

  private UrlCustomUrl parseCustomUrlPsi(
      @NotNull String urlString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlCustomUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.CUSTOM_URL.rootElementType(),
        UrlCustomUrl.class,
        UrlSubParserDefinitions.CUSTOM_URL,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors())
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);

    return urlPsi;
  }

  private void handleCustomRequest(
      @NotNull Resource resource,
      @NotNull UrlCustomUrl urlPsi,
      @NotNull CustomOperation<?> operation,
      @NotNull HttpServerExchange exchange) throws RequestFailedException, IOException {

    CustomRequestUrl customRequestUrl = null;
    PsiProcessingContext psiProcessingContext = new DefaultPsiProcessingContext();
    try {
      customRequestUrl = CustomRequestUrlPsiParser.parseCustomRequestUrl(
          resource.declaration().fieldType(),
          operation.declaration(),
          urlPsi,
          typesResolver,
          psiProcessingContext
      );
    } catch (PsiProcessingException e) {
      psiProcessingContext.setErrors(e.errors());
    }

    if (!psiProcessingContext.errors().isEmpty()) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), psiProcessingContext.errors(), exchange);
    }

    assert customRequestUrl != null;

    final @Nullable ReqInputFieldProjection inputProjection = customRequestUrl.inputProjection();

    // read body
    final Data body;
    JsonParser bodyParser = new JsonFactory().createParser(exchange.getInputStream());
    if (inputProjection == null) {
      final OpInputFieldProjection opInputProjection = operation.declaration().inputProjection();
      if (opInputProjection == null)
        body = null;
      else {
        OpInputJsonFormatReader bodyReader = new OpInputJsonFormatReader(bodyParser);
        body = wrapIAE(exchange, "Error reading request body", () ->
            bodyReader.readData(opInputProjection.varProjection())
        );
      }
    } else {
      ReqInputJsonFormatReader bodyReader = new ReqInputJsonFormatReader(bodyParser);
      body = wrapIAE(exchange, "Error reading request body", () ->
          bodyReader.readData(inputProjection.varProjection())
      );
    }

    // run operation
    final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection =
        customRequestUrl.outputProjection();

    CompletionStage<? extends ReadOperationResponse<?>> future = operation.process(
        new CustomOperationRequest(
            customRequestUrl.path(),
            body,
            inputProjection,
            outputProjection.projection()
        ));

    // send response back
    handleReadResponse(
        StatusCodes.OK,
        outputProjection.pathSteps(),
        outputProjection.projection().varProjection(),
        future,
        exchange
    );
  }

  // util --------------------------------------------------------------------------------------------------------------

  // async timeouts support. Use `onTimeout` instead once on JDK9
  private static final ThreadFactory threadFactory = new ThreadFactory() {
    private final AtomicLong counter = new AtomicLong();

    @Override
    public Thread newThread(final @NotNull Runnable r) {
      Thread t = new Thread(r, "failAfter-" + (counter.incrementAndGet()));
      t.setDaemon(true);
      return t;
    }
  };

  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, threadFactory);

  private static <T> CompletableFuture<T> failAfter(Duration duration) {
    final CompletableFuture<T> promise = new CompletableFuture<>();
    scheduler.schedule(() -> {
      final TimeoutException ex = new TimeoutException("Timeout after " + duration);
      return promise.completeExceptionally(ex);
    }, duration.toMillis(), MILLISECONDS);
    return promise;
  }

  private static <T> T wrapIAE(
      final @NotNull HttpServerExchange exchange,
      final @NotNull String errorMessage,
      final @NotNull Callable<T> closure) throws RequestFailedException {

    try {
      return closure.call();
    } catch (IllegalArgumentException e) {
      LOG.info(errorMessage, e);
      badRequest(errorMessage + " : " + e.getMessage(), CONTENT_TYPE_TEXT, exchange);
      throw RequestFailedException.INSTANCE;
    } catch (Exception e) {
      LOG.info(errorMessage, e);
      serverError(errorMessage + " : " + e.getMessage(), CONTENT_TYPE_TEXT, exchange);
      throw RequestFailedException.INSTANCE;
    }
  }

  private void writeData(
      @NotNull ReqOutputVarProjection projection,
      @Nullable Data data,
      @NotNull OutputStream stream) throws IOException {

    final Writer writer = createWriter(stream);
    JsonFormatWriter fw = new JsonFormatWriter(writer);
    fw.writeData(projection, data);
    writer.close();
  }


  private void writeDatum(
      @NotNull ReqOutputModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull OutputStream outputStream) throws IOException {

    final Writer writer = createWriter(outputStream);
    JsonFormatWriter fw = new JsonFormatWriter(writer);
    fw.writeDatum(projection, datum);
    writer.close();
  }

  private @NotNull String dataToString(@Nullable Data data) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeData(data);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
  }

  private @NotNull Writer createWriter(final @NotNull OutputStream stream) {
    return new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
  }

  @Contract(pure = true)
  private @NotNull String getNullResponse() { return "null"; }

}
