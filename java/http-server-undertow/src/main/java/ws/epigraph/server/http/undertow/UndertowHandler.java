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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.idl.operations.OperationKind;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.server.http.RequestHeaders;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.*;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.url.parser.projections.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.wire.json.JsonFormatWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ws.epigraph.server.http.undertow.Constants.JSON;
import static ws.epigraph.server.http.undertow.Constants.TEXT;
import static ws.epigraph.server.http.undertow.Util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHandler implements HttpHandler {

  private final static Pattern RESOURCE_PATTERN = Pattern.compile("/(\\p{Lower}\\p{Alnum}*)(.*)");
  @NotNull
  private final Service service;
  @NotNull
  private final TypesResolver typesResolver;

  public UndertowHandler(@NotNull Service service, @NotNull TypesResolver typesResolver) {
    this.service = service;
    this.typesResolver = typesResolver;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    final Sender sender = exchange.getResponseSender();

    try {

      String decodedUri = getDecodedRequestString(exchange);
      String resourceName = getResourceName(decodedUri, exchange);

      Resource resource = ResourceRouter.findResource(resourceName, service);

      HttpString requestMethod = getMethod(exchange);
      if (requestMethod.equals(Methods.GET)) {
        UrlReadUrl urlPsi = parseReadPsi(decodedUri, exchange);
        handleReadRequest(resource, urlPsi, exchange);
      } else {
        // todo handle the rest
        badRequest("Unsupported HTTP method '" + requestMethod + "'\n", TEXT, exchange);
        throw RequestFailedException.INSTANCE;
      }

    } catch (ResourceNotFoundException | OperationNotFoundException e) {
      badRequest(e.getMessage(), TEXT, exchange);
    } catch (RequestFailedException ignored) { // already handled
    } catch (Exception e) {
      // todo log, sanitize etc

      e.printStackTrace();
      exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
      sender.send(e.getMessage());

    } finally {
      sender.close();
    }
  }

  @NotNull
  private HttpString getMethod(@NotNull final HttpServerExchange exchange) {return exchange.getRequestMethod();}

  @NotNull
  private String getResourceName(
      @NotNull final String url,
      @NotNull final HttpServerExchange exchange) throws RequestFailedException {

    Matcher matcher = RESOURCE_PATTERN.matcher(url);

    if (!matcher.matches()) {
      badRequest("Bad URL format\n", TEXT, exchange);
      throw RequestFailedException.INSTANCE;
    }

    return matcher.group(1);
  }

  @Nullable
  private String getOperationName(@NotNull HttpServerExchange exchange) {
    final HeaderValues headerValues = exchange.getRequestHeaders().get(RequestHeaders.OPERATION_NAME);
    return headerValues == null ? null : headerValues.getFirst(); // warn if more than one?
  }

  private void handleReadRequest(
      @NotNull Resource resource,
      @NotNull UrlReadUrl urlPsi,
      @NotNull HttpServerExchange exchange)
      throws ResourceNotFoundException, OperationNotFoundException, PsiProcessingException, RequestFailedException {

    try {
      // find operation
      OperationSearchSuccess<ReadOperation<?>> operationSearchResult = findReadOperation(
          resource,
          getOperationName(exchange),
          urlPsi,
          exchange
      );

      // run operation
      CompletableFuture<? extends ReadOperationResponse> future = operationSearchResult.operation().process(
          new ReadOperationRequest(
              operationSearchResult.path(),
              operationSearchResult.stepsAndProjection().projection()
          ));

      // send response back
      handleReadResponse(
          operationSearchResult.stepsAndProjection().pathSteps(),
          operationSearchResult.stepsAndProjection().projection().projection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      reportPsiProcessingErrorsAndFail(urlPsi.getText(), e.errors(), exchange);
    }
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<ReadOperation<?>> findReadOperation(
      final @NotNull Resource resource,
      final @Nullable String operationName,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull HttpServerExchange exchange)
      throws PsiProcessingException, OperationNotFoundException, RequestFailedException {

    final OperationSearchResult searchResult =
        ReadOperationRouter.findReadOperation(operationName, urlPsi, resource, typesResolver);

    if (searchResult instanceof OperationNotFound<?>)
      throw new OperationNotFoundException(resource.declaration().fieldName(), OperationKind.READ, operationName);

    if (searchResult instanceof OperationSearchFailure<?>) {
      Util.reportOperationSearchFailureAndFail(urlPsi.getText(), (OperationSearchFailure<?>) searchResult, exchange);
    }

    assert searchResult instanceof OperationSearchSuccess;
    return ((OperationSearchSuccess<ReadOperation<?>>) searchResult);
  }

  private void handleReadResponse(
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull CompletableFuture<? extends ReadOperationResponse> responseFuture,
      @NotNull final HttpServerExchange exchange
  ) {
    // todo set future timeout

    responseFuture.whenComplete((readOperationResponse, throwable) -> {
      Sender sender = exchange.getResponseSender();

      if (throwable == null) {

        @Nullable Data data = readOperationResponse.getData();
        writeDataResponse(pathSteps, reqProjection, data, exchange);

      } else {
        // todo this can be a non-500

        serverError(throwable.getMessage(), TEXT, exchange);
      }

      sender.close();
    });
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

        if (noPathData.error != null) {
          contentType = "text/plain"; // todo report errors in json too?
          statusCode = noPathData.error.statusCode();
          responseText = noPathData.error.message();

          @Nullable final Exception cause = noPathData.error.cause;
          if (cause != null) {
            responseText = responseText + "\ncaused by: " + cause.toString();
            //add stacktrace too?
          }
        } else {
          final OutputProjectionPathRemover.PathRemovalResult noPathProjection =
              OutputProjectionPathRemover.removePath(reqProjection, pathSteps);

          @Nullable final ReqOutputVarProjection varProjection = noPathProjection.varProjection();
          @Nullable final ReqOutputModelProjection<?, ?> modelProjection = noPathProjection.modelProjection();

          // todo this must be streaming
          if (varProjection != null) {
            responseText = dataToString(varProjection, noPathData.data);
          } else if (modelProjection != null) {
            responseText = datumToString(modelProjection, noPathData.datum);
          } else {
            responseText = getNullResponse();
          }
        }

      }

      writeResponse(statusCode, responseText + "\n", contentType, exchange);

    } catch (AmbiguousPathException e) {
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
      // todo log, sanitize etc
      e.printStackTrace();
      final String message = e.getMessage();
      serverError(message == null ? null : message + "\n", TEXT, exchange);

    } finally {
      exchange.getResponseSender().close();
    }
  }

  @NotNull
  private String dataToString(@NotNull ReqOutputVarProjection projection, @Nullable Data data) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeData(projection, data);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
  }

  @NotNull
  private String datumToString(@NotNull ReqOutputModelProjection<?, ?> projection, @Nullable Datum datum) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeDatum(projection, datum);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
  }

  @NotNull
  private String dataToString(@Nullable Data data) {
    StringWriter sw = new StringWriter();
    JsonFormatWriter fw = new JsonFormatWriter(sw);
    try {
      fw.writeData(data);
    } catch (IOException e) {
      return e.toString();
    }
    return sw.toString();
  }

  @NotNull
  private String getNullResponse() { return "null"; }

  private UrlReadUrl parseReadPsi(
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

}
