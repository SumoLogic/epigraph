package io.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.printers.DataPrinter;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.service.*;
import io.epigraph.service.operations.ReadOperation;
import io.epigraph.service.operations.ReadOperationRequest;
import io.epigraph.service.operations.ReadOperationResponse;
import io.epigraph.url.ReadRequestUrl;
import io.epigraph.url.ReadRequestUrlPsiParser;
import io.epigraph.url.RequestUrl;
import io.epigraph.url.parser.projections.UrlSubParserDefinitions;
import io.epigraph.url.parser.psi.UrlReadUrl;
import io.epigraph.wire.json.JsonFormatWriter;
import io.undertow.UndertowOptions;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHandler implements HttpHandler {
  public static final String TEXT = "text/plain";
  public static final String JSON = "application/json";
  public static final String HTML = "text/html";

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

//    HeaderMap responseHeaders = exchange.getResponseHeaders();
//    responseHeaders.put(Headers.CONTENT_TYPE, "text/plain"); // todo

    try {
      HttpString requestMethod = exchange.getRequestMethod();

      String decodedUri = getDecodedRequestString(exchange);

      // extract resource name and the rest of the path
      Matcher matcher = RESOURCE_PATTERN.matcher(decodedUri);

      if (!matcher.matches()) {
        badRequest("Bad URL format\n", TEXT, exchange);
        throw RequestFailedException.INSTANCE;
      }

      String resourceName = matcher.group(1);
      Resource resource = ResourceRouter.findResource(resourceName, service);

      UrlReadUrl urlPsi = parseReadPsi(decodedUri, exchange);

      if (requestMethod.equals(Methods.GET)) {
        handleReadRequest(resource, urlPsi, exchange);
        // todo handle the rest
      } else {
        badRequest("Unknown HTTP method '" + requestMethod + "'\n", TEXT, exchange);
        throw RequestFailedException.INSTANCE;
      }

    } catch (ResourceNotFoundException | OperationNotFoundException e) {
      badRequest(e.getMessage(), TEXT, exchange);
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

  @NotNull
  private String getDecodedRequestString(@NotNull HttpServerExchange exchange) {
    // any way to disable request parsing in Undertow? we don't really need it..

    final String uri = exchange.getRequestURI(); // this doesn't include query params?!
    final String queryString = exchange.getQueryString();

    final String encodedReq;

    if (queryString == null || queryString.isEmpty()) encodedReq = uri;
    else encodedReq = uri + "%3f" + queryString; // question mark gets removed

    return URLUtils.decode(
        encodedReq,
        exchange.getConnection().getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()),
        false,
        new StringBuilder()
    );
  }

  private void handleReadRequest(
      @NotNull Resource resource,
      @NotNull UrlReadUrl urlPsi,
      @NotNull HttpServerExchange exchange)
      throws ResourceNotFoundException, OperationNotFoundException, RequestFailedException {

    @Nullable String operationName = getOperationName(exchange.getQueryParameters());
    @NotNull ReadOperation operation = ResourceRouter.findReadOperation(operationName, resource);

    @NotNull ResourceIdl resourceDeclaration = resource.declaration();
    @NotNull ReadOperationIdl operationDeclaration = operation.declaration();

    try {
      @Nullable
      final RequestUrl requestUrl = parseReadRequestUrl(urlPsi, resourceDeclaration, operationDeclaration);

      // parse output projection
      @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = requestUrl.outputProjection();

      // run operation
      CompletableFuture<? extends ReadOperationResponse> future =
          operation.process(new ReadOperationRequest(null, stepsAndProjection.projection()));

      // send response back
      handleReadResponse(
          stepsAndProjection.pathSteps(),
          stepsAndProjection.projection().projection(),
          future,
          exchange
      );
    } catch (PsiProcessingException e) {
      StringBuilder sb = new StringBuilder();
      @NotNull TextLocation textRange = e.location();
      String errorDescription = e.getMessage();

      if (htmlAccepted(exchange)) {
        appendHtmlErrorHeader(sb);
        addPsiErrorHtml(sb, urlPsi.getText(), textRange, errorDescription);
        appendHtmlErrorFooter(sb);
        badRequest(sb.toString(), HTML, exchange);
      } else {
        addPsiErrorPlainText(sb, urlPsi.getText(), textRange, errorDescription);
        badRequest(sb.toString(), TEXT, exchange);
      }

      throw RequestFailedException.INSTANCE;
    }
  }

  @NotNull
  private RequestUrl parseReadRequestUrl(
      @NotNull UrlReadUrl urlPsi,
      @NotNull ResourceIdl resourceDeclaration,
      @NotNull ReadOperationIdl operationDeclaration) throws PsiProcessingException {

    @Nullable
    final ReadRequestUrl requestUrl = ReadRequestUrlPsiParser.parseReadRequestUrl(
        resourceDeclaration.fieldType(),
        operationDeclaration,
        urlPsi,
        typesResolver
    );

    assert requestUrl != null; // or else what?
    return requestUrl;
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
    int stepsToRemove = pathSteps == 0 ? 0 : pathSteps - 1; // last segment of path = our data, so keep it

    String contentType = JSON; // todo should depend on marshaller
    int statusCode = 200;
    @NotNull String responseText;

    try {
      if (trimmedData == null) {
        responseText = getNullResponse();
      } else {
        DataPathRemover.PathRemovalResult noPathData = DataPathRemover.removePath(trimmedData, stepsToRemove);

        // todo marshal to proper json or whatever
        if (stepsToRemove <= 1) { // FIXME - use path-traversed projection always
          responseText = dataToString(reqProjection, data);
        } else {
          if (noPathData.data != null) {
            responseText = dataToString(noPathData.data);
          } else if (noPathData.datum != null) {
            responseText = datumToString(noPathData.datum);
          } else if (noPathData.error != null) {
            contentType = "text/plain"; // todo report errors in json too?
            statusCode = noPathData.error.statusCode();
            responseText = noPathData.error.message();

            @Nullable final Exception cause = noPathData.error.cause;
            if (cause != null) {
              responseText = responseText + "\ncaused by: " + cause.toString();
              //add stacktrace too?
            }
          } else {
            responseText = getNullResponse();
          }
        }
      }

      writeResponse(statusCode, responseText + "\n", contentType, exchange);

    } catch (DataPathRemover.AmbiguousPathException e) {
      serverError(
          String.format(
              "Can't remove %d path steps from data: \n%s\n",
              stepsToRemove,
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

  private void badRequest(@Nullable String message, @NotNull String contentType, @NotNull HttpServerExchange exchange) {
    writeResponse(StatusCodes.BAD_REQUEST, message, contentType, exchange);
  }

  private void serverError(
      @Nullable String message,
      @NotNull String contentType,
      @NotNull HttpServerExchange exchange) {
    writeResponse(StatusCodes.INTERNAL_SERVER_ERROR, message, contentType, exchange);
  }

  private void writeResponse(
      int statusCode,
      @Nullable String response,
      @NotNull String contentType,
      @NotNull HttpServerExchange exchange) {

    exchange.setStatusCode(statusCode);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
    if (response != null) exchange.getResponseSender().send(response);
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

  @Deprecated
  @NotNull
  private String dataToString(@Nullable Data data) {
    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    DataPrinter<NoExceptions> dp = new DataPrinter<>(l);
    dp.print(data);
    l.close();
    return sb.getString();
  }

  @Deprecated
  @NotNull
  private String datumToString(@Nullable Datum datum) {
    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    DataPrinter<NoExceptions> dp = new DataPrinter<>(l);
    dp.print(datum);
    l.close();
    return sb.getString();
  }

  @NotNull
  private String getNullResponse() { return "null"; }

  @Nullable
  private String getOperationName(@NotNull Map<String, Deque<String>> parameters) {
    Deque<String> deque = parameters.get(RequestParameters.OPERATION_NAME);
    if (deque == null || deque.isEmpty()) return null;
    // todo fail if more than one specified
    return deque.iterator().next();
  }

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
      // try to highlight errors

      final boolean htmlAccepted = htmlAccepted(exchange);

      StringBuilder sb = new StringBuilder();
      if (htmlAccepted) appendHtmlErrorHeader(sb);

      boolean first = true;
      for (PsiErrorElement errorElement : errorsAccumulator.errors()) {

        if (first) {
          first = false;
        } else {
          if (htmlAccepted) sb.append("<br/><br/>");
          else sb.append('\n');
        }

        TextLocation textLocation = EpigraphPsiUtil.getLocation(errorElement);
        String errorDescription = errorElement.getErrorDescription();
        if (htmlAccepted)
          addPsiErrorHtml(sb, urlString, textLocation, errorDescription);
        else
          addPsiErrorPlainText(sb, urlString, textLocation, errorDescription);
      }

      if (htmlAccepted) {
        appendHtmlErrorFooter(sb);
        badRequest(sb.toString(), HTML, exchange);
      } else {
        badRequest(sb.toString(), TEXT, exchange);
      }


      throw RequestFailedException.INSTANCE;
    }

    return urlPsi;
  }


  private void addPsiErrorPlainText(
      StringBuilder sb,
      String projectionString,
      TextLocation textLocation,
      String errorDescription) {

    final int startOffset = textLocation.startOffset();
    final int endOffset = textLocation.endOffset();

    sb.append(errorDescription).append("\n\n");

    if (startOffset != 0 || endOffset != 0) {
      int i = 0;

      sb.append(projectionString).append('\n');


      while (i < startOffset) {
        sb.append(' ');
        i++;
      }

      while (i < endOffset) {
        sb.append('^');
        i++;
      }

      if (startOffset == endOffset) sb.append('^'); // fix grammar so this never happens?

      sb.append('\n');
    }
  }

  private void addPsiErrorHtml(
      StringBuilder sb,
      String projectionString,
      TextLocation textLocation,
      String errorDescription) {

    int startOffset = textLocation.startOffset();
    int endOffset = textLocation.endOffset();

    sb.append(errorDescription).append("<br/><br/>");

    if (startOffset != 0 || endOffset != 0) {
      if (startOffset == endOffset) {
        endOffset++;
        if (endOffset > projectionString.length()) {
          projectionString += " "; // to have something to point to at the end of the string
        }
      }

      String s1 = projectionString.substring(0, startOffset);
      String s2 = projectionString.substring(startOffset, endOffset);
      String s3 = projectionString.substring(endOffset);

      sb.append(s1);
      sb.append("<div class=\"err\">").append(s2).append("</div>");
      sb.append(s3);

      sb.append("<br/>");
    }
  }

  private void appendHtmlErrorHeader(@NotNull StringBuilder sb) {
    sb.append("<!DOCTYPE html><head><style>")
      .append("body {")
      .append("  font-family: monospace;")
      .append("}")
      .append("")
      .append(".err {")
      .append("  border-bottom:2px dotted red;")
      .append("  display: inline-block;")
      .append("  position: relative;")
      .append("}")
      .append("")
      .append(".err:after {")
      .append("  content: '';")
      .append("  width: 100%;")
      .append("  height: 5px;")
      .append("  border-bottom:2px dotted red;")
      .append("  position: absolute;")
      .append("  bottom: -3px;")
      .append("  left: -2px;")
      .append("  display: inline-block;")
      .append("}")
      .append("</style></head><body>");
  }

  private void appendHtmlErrorFooter(@NotNull StringBuilder sb) {
    sb.append("</body>");
  }

  private boolean htmlAccepted(@NotNull HttpServerExchange exchange) {
    final HeaderValues contentTypeHeader = exchange.getRequestHeaders().get(Headers.ACCEPT);
    if (contentTypeHeader == null) return false;
    for (String header : contentTypeHeader) if (header.toLowerCase().contains(HTML)) return true;
    return false;
  }

  private static class RequestFailedException extends Exception {
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    public static final RequestFailedException INSTANCE = new RequestFailedException();
  }
}
