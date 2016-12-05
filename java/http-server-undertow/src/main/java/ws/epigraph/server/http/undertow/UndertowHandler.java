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
import ws.epigraph.idl.operations.OperationKind;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.server.http.RequestHeaders;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.*;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.url.parser.psi.UrlUrl;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ws.epigraph.server.http.undertow.Constants.JSON;
import static ws.epigraph.server.http.undertow.Constants.TEXT;
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
  private final @NotNull ReadOperationRouter readOperationRouter = new ReadOperationRouter();

  public UndertowHandler(@NotNull Service service, @NotNull TypesResolver typesResolver, long responseTimeout) {
    this.service = service;
    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) {
    final Sender sender = exchange.getResponseSender();

    try {
      String decodedUri = getDecodedRequestString(exchange);
      String resourceName = getResourceName(decodedUri, exchange);

      Resource resource = ResourceRouter.findResource(resourceName, service);

      HttpString requestMethod = getMethod(exchange);
      if (requestMethod.equals(Methods.GET)) {
        UrlReadUrl urlPsi = parseReadUrlPsi(decodedUri, exchange);
        handleReadRequest(resource, urlPsi, exchange);
      } else {
        // todo handle the rest
        badRequest("Unsupported HTTP method '" + requestMethod + "'\n", TEXT, exchange);
        //noinspection ThrowCaughtLocally
        throw RequestFailedException.INSTANCE;
      }

    } catch (ResourceNotFoundException e) {
      badRequest(e.getMessage() + ". Supported resources: {" + listSupportedResources(service) + "}", TEXT, exchange);
    } catch (OperationNotFoundException e) {
      badRequest(e.getMessage(), TEXT, exchange);
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
  private @NotNull HttpString getMethod(final @NotNull HttpServerExchange exchange) {
    return exchange.getRequestMethod();
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
          , TEXT, exchange);
      throw RequestFailedException.INSTANCE;
    }

    return matcher.group(1);
  }

  private @Nullable String getOperationName(@NotNull HttpServerExchange exchange) {
    final HeaderValues headerValues = exchange.getRequestHeaders().get(RequestHeaders.OPERATION_NAME);
    return headerValues == null ? null : headerValues.getFirst(); // warn if more than one?
  }

  private <
      U extends RequestUrl,
      UP extends UrlUrl,
      R extends AbstractOperationRouter<UP, ?, ?, U>
      >
  void handleRequest(
      @NotNull Resource resource,
      @NotNull UrlReadUrl urlPsi,
      @NotNull HttpServerExchange exchange)
      throws ResourceNotFoundException, OperationNotFoundException, PsiProcessingException, RequestFailedException {

    try {
      // find operation
      OperationSearchSuccess<ReadOperation<?>, ReadRequestUrl> operationSearchResult = findReadOperation(
          resource,
          getOperationName(exchange),
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
          outputProjection.pathSteps(),
          outputProjection.projection().varProjection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
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
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @Nullable Data data,
      @NotNull HttpServerExchange exchange) {

    // todo validate response: e.g. all required parts must be present

    Data trimmedData = data == null ? null : ProjectionDataTrimmer.trimData(data, reqProjection);

    String contentType = JSON; // todo should depend on marshaller
    int statusCode = 200;
    @NotNull String responseText;

    try {
      if (trimmedData == null) {
        responseText = getNullResponse();
      } else {
        DataPathRemover.PathRemovalResult noPathData = DataPathRemover.removePath(trimmedData, pathSteps);

        if (noPathData.error == null) {
          final OutputProjectionPathRemover.PathRemovalResult noPathProjection =
              OutputProjectionPathRemover.removePath(reqProjection, pathSteps);

          final @Nullable ReqOutputVarProjection varProjection = noPathProjection.varProjection();
          final @Nullable ReqOutputModelProjection<?, ?> modelProjection = noPathProjection.modelProjection();

          // todo this must be streaming
          if (varProjection != null) {
            responseText = dataToString(varProjection, noPathData.data);
          } else if (modelProjection != null) {
            responseText = datumToString(modelProjection, noPathData.datum);
          } else {
            responseText = getNullResponse();
          }
        } else {
          contentType = "text/plain"; // todo report errors in json too?
          statusCode = noPathData.error.statusCode();
          responseText = noPathData.error.message();

          final @Nullable Exception cause = noPathData.error.cause;
          if (cause != null) {
            responseText = responseText + "\ncaused by: " + cause.toString();
            //add stacktrace too?
          }
        }

      }

      writeResponse(statusCode, responseText + "\n", contentType, exchange);

    } catch (AmbiguousPathException ignored) {
      serverError(
          String.format(
              "Can't remove %d path steps from data: \n%s\n",
              pathSteps == 0 ? 0 : pathSteps - 1,
              dataToString(trimmedData)
          ),
          TEXT,
          exchange
      );
    } catch (Exception e) {
      LOG.error("Error writing response", e);
      final String message = e.getMessage();
      serverError(message == null ? null : message + "\n", TEXT, exchange);
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

    if (errorsAccumulator.hasErrors()) {
      reportPsiProcessingErrorsAndFail(urlString, psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()), exchange);
    }

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<ReadOperation<?>, ReadRequestUrl> findReadOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    return findOperation(resource, operationName, urlPsi, readOperationRouter, OperationKind.READ, exchange);
  }

  private void handleReadRequest(
      @NotNull Resource resource,
      @NotNull UrlReadUrl urlPsi,
      @NotNull HttpServerExchange exchange)
      throws ResourceNotFoundException, OperationNotFoundException, PsiProcessingException, RequestFailedException {

    handleRequest(resource, urlPsi, exchange);
  }

  private <R extends ReadOperationResponse<?>> void handleReadResponse(
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull CompletionStage<R> responseFuture,
      final @NotNull HttpServerExchange exchange
  ) {

    final Consumer<R> resultConsumer = readOperationResponse -> {
      Sender sender = exchange.getResponseSender();

      try {
        @Nullable Data data = readOperationResponse.getData();
        writeDataResponse(pathSteps, reqProjection, data, exchange);
      } catch (Exception e) {
        LOG.error("Error processing request", e);
        serverError(e.getMessage(), TEXT, exchange);
      } finally {
        sender.close();
      }

    };

    final Function<Throwable, Void> failureConsumer = throwable -> {
      serverError(throwable.getMessage(), TEXT, exchange);
      return null;
    };

    if (responseTimeout > 0) {
      CompletionStage<R> timeout = failAfter(Duration.ofMillis(responseTimeout));
      responseFuture.acceptEither(timeout, resultConsumer).exceptionally(failureConsumer);
    } else {
      responseFuture.thenAccept(resultConsumer).exceptionally(failureConsumer);
    }
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

  private @NotNull String dataToString(@NotNull ReqOutputVarProjection projection, @Nullable Data data) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeData(projection, data);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
  }

  private @NotNull String datumToString(@NotNull ReqOutputModelProjection<?, ?> projection, @Nullable Datum datum) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeDatum(projection, datum);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
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

  @Contract(pure = true)
  private @NotNull String getNullResponse() { return "null"; }

}
