package io.epigraph.server.http;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiErrorElement;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import io.epigraph.idl.parser.psi.IdlReqOutputTrunkFieldProjection;
import io.epigraph.printers.DataPrinter;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.output.ReqOutputProjectionsPsiParser;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.service.*;
import io.epigraph.service.operations.ReadOperation;
import io.epigraph.service.operations.ReadOperationRequest;
import io.epigraph.service.operations.ReadOperationResponse;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHandler implements HttpHandler {
  private final static Pattern RESOURCE_PATTERN = Pattern.compile("/(\\p{Lower}(\\p{Lower}|\\d)*)(.*)");
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
    HeaderMap responseHeaders = exchange.getResponseHeaders();

    responseHeaders.put(Headers.CONTENT_TYPE, "text/plain"); // todo

    try {
      HttpString requestMethod = exchange.getRequestMethod();
      String url = exchange.getRequestURI();

      // extract resource name and the rest of the path
      Matcher matcher = RESOURCE_PATTERN.matcher(url);

      if (!matcher.matches()) {
        badRequest(null, exchange);
        throw RequestFailedException.INSTANCE;
      }

      String resourceName = matcher.group(1);
      String resourceProjectionString = matcher.group(3);
//      if (resourceProjectionString.length() > 0) resourceProjectionString = resourceProjectionString.substring(1); // remove leading '/'

      Resource resource = ResourceRouter.findResource(resourceName, service);

      IdlReqOutputTrunkFieldProjection outputPsi = parsePsi(resourceProjectionString, exchange);

      if (requestMethod.equals(Methods.GET)) {
        handleReadRequest(resource, outputPsi, exchange);
      } else {
        badRequest("Unknown HTTP method '" + requestMethod + "'", exchange);
        throw RequestFailedException.INSTANCE;
      }

    } catch (ResourceNotFoundException | OperationNotFoundException e) {
      badRequest(e.getMessage(), exchange);
      sender.close();
    } catch (RequestFailedException ignored) {
      sender.close();
    } catch (Exception e) {
      // todo log, sanitize etc

      e.printStackTrace();
      exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
      sender.send(e.getMessage());

      sender.close();
    }
  }

  private void handleReadRequest(@NotNull Resource resource,
                                 @NotNull IdlReqOutputTrunkFieldProjection outputPsi,
                                 @NotNull HttpServerExchange exchange)
      throws ResourceNotFoundException, OperationNotFoundException, RequestFailedException {

    @Nullable String operationName = getOperationName(exchange.getQueryParameters());
    @NotNull ReadOperation operation = ResourceRouter.findReadOperation(operationName, resource);

    @NotNull ResourceIdl resourceDeclaration = resource.declaration();
    @NotNull ReadOperationIdl operationDeclaration = operation.declaration();

    try {
      @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
          ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
              true,
              resourceDeclaration.fieldType(),
              operationDeclaration.params(),
              operationDeclaration.outputProjection(),
              outputPsi,
              typesResolver
          );

      CompletableFuture<ReadOperationResponse> future =
          operation.process(new ReadOperationRequest(stepsAndProjection.projection()));


      handleReadResponse(stepsAndProjection.pathSteps(),
                         stepsAndProjection.projection().projection(),
                         future,
                         exchange
      );
    } catch (PsiProcessingException e) {
      StringBuilder sb = new StringBuilder(outputPsi.getText());
      sb.append('\n');
      TextRange textRange = e.psi().getTextRange();
      String errorDescription = e.getMessage();
      addPsiError(sb, textRange, errorDescription);

      badRequest(sb.toString(), exchange);


      throw RequestFailedException.INSTANCE;
    }
  }

  private void handleReadResponse(
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull CompletableFuture<ReadOperationResponse> responseFuture,
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

        serverError(throwable.getMessage(), exchange);
      }

      sender.close();
    });
  }

  private void writeDataResponse(final int pathSteps,
                                 @NotNull ReqOutputVarProjection reqProjection,
                                 @Nullable Data data,
                                 @NotNull HttpServerExchange exchange) {

    // todo validate response: e.g. all required parts must be present

    Data trimmedData = data == null ? null : ProjectionDataTrimmer.trimData(data, reqProjection);

    if (trimmedData == null) {
      writeNullResponse(exchange.getResponseSender());
    } else {
      try {
        DataPathRemover.PathRemovalResult noPathData = DataPathRemover.removePath(trimmedData, pathSteps);

        // todo marshal to proper json or whatever
        if (noPathData.data != null) {
          exchange.getResponseSender().send(dataToString(noPathData.data));
        } else if (noPathData.datum != null) {
          exchange.getResponseSender().send(datumToString(noPathData.datum));
        } else if (noPathData.error != null) {
          @Nullable final Integer statusCode = noPathData.error.statusCode();
          if (statusCode != null) exchange.setStatusCode(statusCode);

          @Nullable final Exception cause = noPathData.error.cause;
          if (cause != null) {
            exchange.getResponseSender().send(cause.getMessage());
            //send stacktrace too?
          }
        } else writeNullResponse(exchange.getResponseSender());

      } catch (DataPathRemover.AmbiguousPathException e) {
        serverError("Can't remove " + pathSteps + " path steps from data: \n" + dataToString(data), exchange);
      }
    }
  }

  @NotNull
  private String dataToString(@Nullable Data data) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    DataPrinter<NoExceptions> dp = new DataPrinter<>(l);
    dp.print(data);
    l.close();
    return sb.getString();
  }

  @NotNull
  private String datumToString(@Nullable Datum datum) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    DataPrinter<NoExceptions> dp = new DataPrinter<>(l);
    dp.print(datum);
    l.close();
    return sb.getString();
  }

  private void writeNullResponse(@NotNull Sender sender) {
    sender.send("null");
  }

  @Nullable
  private String getOperationName(@NotNull Map<String, Deque<String>> parameters) {
    Deque<String> deque = parameters.get(RequestParameters.OPERATION_NAME);
    if (deque == null || deque.isEmpty()) return null;
    // todo fail if more than one specified
    return deque.iterator().next();
  }

  private IdlReqOutputTrunkFieldProjection parsePsi(
      @NotNull String projectionString,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlReqOutputTrunkFieldProjection psiFieldProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.REQ_OUTPUT_FIELD_PROJECTION.rootElementType(),
        IdlReqOutputTrunkFieldProjection.class,
        IdlSubParserDefinitions.REQ_OUTPUT_FIELD_PROJECTION,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      badRequest(null, exchange);

      // try to highlight errors
      Sender sender = exchange.getResponseSender();

      StringBuilder sb = new StringBuilder();
      for (PsiErrorElement errorElement : errorsAccumulator.errors()) {
        if (sb.length() > 0) sb.append('\n');

        sb.append(projectionString);
        sb.append('\n');
        TextRange textRange = errorElement.getTextRange();
        String errorDescription = errorElement.getErrorDescription();
        addPsiError(sb, textRange, errorDescription);
      }

      sender.send(sb.toString());

      throw RequestFailedException.INSTANCE;
    }

    return psiFieldProjection;
  }

  private void badRequest(@Nullable String message, @NotNull HttpServerExchange exchange) {
    failRequest(StatusCodes.BAD_REQUEST, message, exchange);
  }

  private void serverError(@Nullable String message, @NotNull HttpServerExchange exchange) {
    failRequest(StatusCodes.INTERNAL_SERVER_ERROR, message, exchange);
  }

  private void failRequest(int code, @Nullable String message, @NotNull HttpServerExchange exchange) {
    exchange.setStatusCode(code);
    if (message != null) exchange.getResponseSender().send(message);
  }

  private void addPsiError(StringBuilder sb, TextRange textRange, String errorDescription) {
    int i = 0;

    while (i < textRange.getStartOffset()) {
      sb.append(' ');
      i++;
    }

    while (i < textRange.getEndOffset()) {
      sb.append('~');
      i++;
    }

    sb.append("  ").append(errorDescription);
  }

  private static class RequestFailedException extends Exception {
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    public static final RequestFailedException INSTANCE = new RequestFailedException();
  }
}
