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

package ws.epigraph.server.http.undertow;

import com.fasterxml.jackson.core.JsonFactory;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.invocation.OperationInvocationErrorImpl;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.AbstractHttpServer;
import ws.epigraph.server.http.HtmlCapableOperationInvocationError;
import ws.epigraph.server.http.HttpInvocationContext;
import ws.epigraph.server.http.RequestHeaders;
import ws.epigraph.service.Service;
import ws.epigraph.util.HttpStatusCode;
import ws.epigraph.wire.FormatWriter;
import ws.epigraph.wire.OpInputFormatReader;
import ws.epigraph.wire.ReqInputFormatReader;
import ws.epigraph.wire.ReqUpdateFormatReader;
import ws.epigraph.wire.json.reader.OpInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import static ws.epigraph.server.http.Constants.CONTENT_TYPE_HTML;
import static ws.epigraph.server.http.Constants.CONTENT_TYPE_TEXT;
import static ws.epigraph.server.http.Util.decodeUri;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphUndertowHandler
    extends AbstractHttpServer<EpigraphUndertowHandler.UndertowInvocationContext> implements HttpHandler {

  private static final Logger LOG = LoggerFactory.getLogger(EpigraphUndertowHandler.class); // assuming a thread-safe backend

  private final @NotNull JsonFactory jsonFactory = new JsonFactory();
  private final @NotNull TypesResolver typesResolver;
  private final long responseTimeout;

  public EpigraphUndertowHandler(@NotNull Service service, @NotNull TypesResolver typesResolver, final long responseTimeout) {
    super(service, OperationFilterChains.defaultLocalFilterChains()); // make configurable?
    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;
  }

  @Override
  public void handleRequest(final HttpServerExchange exchange) {
    // dispatch to a worker thread so we can go to blocking mode and enable streaming
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    exchange.startBlocking();
    final UndertowInvocationContext context = new UndertowInvocationContext(exchange);

    final HttpMethod method = getMethod(exchange);
    if (method == null) {
      writeInvocationErrorResponse(
          new OperationInvocationErrorImpl(
              String.format("Unsupported HTTP method '%s'", exchange.getRequestMethod()),
              HttpStatusCode.BAD_REQUEST
          ), context
      );
    } else {
      try {
        handleRequest(getDecodedRequestString(exchange), method, getOperationName(exchange), context);
      } catch (URISyntaxException e) {
        writeInvocationErrorResponse(
            new OperationInvocationErrorImpl(
                String.format("Invalid URI syntax '%s'", e.getMessage()),
                HttpStatusCode.BAD_REQUEST
            ), context
        );
      }
    }
  }

  @Override
  protected long responseTimeout(final @NotNull UndertowInvocationContext context) {
    return responseTimeout;
  }

  @Override
  protected OpInputFormatReader opInputReader(final @NotNull UndertowInvocationContext context) throws IOException {
    return new OpInputJsonFormatReader(jsonFactory.createParser(context.exchange.getInputStream()));
  }

  @Override
  protected ReqInputFormatReader reqInputReader(final @NotNull UndertowInvocationContext context) throws IOException {
    return new ReqInputJsonFormatReader(jsonFactory.createParser(context.exchange.getInputStream()));
  }

  @Override
  protected ReqUpdateFormatReader reqUpdateReader(final @NotNull UndertowInvocationContext context) throws IOException {
    return new ReqUpdateJsonFormatReader(jsonFactory.createParser(context.exchange.getInputStream()));
  }

  @Override
  protected void writeFormatResponse(
      int statusCode,
      @NotNull UndertowInvocationContext context,
      @NotNull FormatResponseWriter formatWriter) {

    try {
      OutputStream outputStream = context.exchange.getOutputStream();
      FormatWriter writer = new JsonFormatWriter(outputStream); // todo make configurable

      HttpServerExchange exchange = context.exchange;
      exchange.setStatusCode(statusCode);
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, writer.httpContentType());

      formatWriter.write(writer);
      writer.close();
    } catch (IOException e) {
      LOG.error("Error writing response", e);
    }
  }

  @Override
  protected void writeInvocationErrorResponse(
      final @NotNull OperationInvocationError error,
      final @NotNull UndertowInvocationContext context) {

    final HttpServerExchange exchange = context.exchange;
    exchange.setStatusCode(error.statusCode());

    if (error instanceof HtmlCapableOperationInvocationError && htmlAccepted(exchange)) {
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, CONTENT_TYPE_HTML);
      exchange.getResponseSender().send(((HtmlCapableOperationInvocationError) error).htmlMessage());
    } else {
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, CONTENT_TYPE_TEXT);
      exchange.getResponseSender().send(error.message() + "\n");
    }

  }

  @Override
  protected void close(final @NotNull EpigraphUndertowHandler.@NotNull UndertowInvocationContext context) throws IOException {
    context.exchange.endExchange();
  }

  private static @Nullable HttpMethod getMethod(@NotNull HttpServerExchange exchange) {
    final HttpString method = exchange.getRequestMethod();
    if (method.equals(Methods.GET)) return HttpMethod.GET;
    if (method.equals(Methods.POST)) return HttpMethod.POST;
    if (method.equals(Methods.PUT)) return HttpMethod.PUT;
    if (method.equals(Methods.DELETE)) return HttpMethod.DELETE;
    return null;
  }

  private @Nullable String getOperationName(@NotNull HttpServerExchange exchange) {
    final HeaderValues headerValues = exchange.getRequestHeaders().get(RequestHeaders.OPERATION_NAME);
    return headerValues == null ? null : headerValues.getFirst(); // warn if more than one?
  }

  private static boolean htmlAccepted(@NotNull HttpServerExchange exchange) {
    final HeaderValues contentTypeHeader = exchange.getRequestHeaders().get(Headers.ACCEPT);
    if (contentTypeHeader == null) return false;
    for (String header : contentTypeHeader) if (header.toLowerCase().contains(CONTENT_TYPE_HTML)) return true;
    return false;
  }

  class UndertowInvocationContext implements HttpInvocationContext {
    final @NotNull HttpServerExchange exchange;

    UndertowInvocationContext(@NotNull HttpServerExchange exchange) {
      this.exchange = exchange;
    }

    @Override
    public Logger logger() { return LOG; }

    @Override
    public TypesResolver typesResolver() { return typesResolver; }

    @Override
    public boolean isDebug() {
      final HeaderValues headerValues = exchange.getRequestHeaders().get(RequestHeaders.DEBUG_MODE);
      return headerValues != null && "true".equals(headerValues.getFirst());
    }

  }

  private static @NotNull String getDecodedRequestString(@NotNull HttpServerExchange exchange)
      throws URISyntaxException {
    // any way to disable request parsing in Undertow? we don't really need it..

    final String uri = exchange.getRequestURI(); // this doesn't include query params?!
    final String queryString = exchange.getQueryString();

    final String encodedReq;

    if (queryString == null || queryString.isEmpty()) encodedReq = uri;
    else encodedReq = uri + "?" + queryString; // question mark gets removed

    return decodeUri(encodedReq);
  }

}
