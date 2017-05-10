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

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.http.EpigraphHeaders;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationErrorImpl;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.*;
import ws.epigraph.service.Service;
import ws.epigraph.util.HttpStatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Function;

import static ws.epigraph.server.http.Util.decodeUri;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphUndertowHandler
    extends AbstractHttpServer<EpigraphUndertowHandler.UndertowInvocationContext> implements HttpHandler {

  private static final Logger LOG = LoggerFactory.getLogger(EpigraphUndertowHandler.class);

  private final @NotNull TypesResolver typesResolver;
  private final long responseTimeout;

  public EpigraphUndertowHandler(@NotNull Service service, final long responseTimeout) {
    this(service, IndexBasedTypesResolver.INSTANCE, responseTimeout);
  }

  public EpigraphUndertowHandler(
      @NotNull Service service,
      @NotNull TypesResolver typesResolver,
      final long responseTimeout) {

    this(
        service,
        typesResolver,
        new FormatBasedServerProtocol.Factory<>(),
        DefaultFormats.instance(formatNameExtractor()),
        responseTimeout
    );
  }

  public EpigraphUndertowHandler(
      @NotNull Service service,
      @NotNull TypesResolver typesResolver,
      @NotNull FormatBasedServerProtocol.Factory<UndertowInvocationContext> serverProtocolFactory,
      @NotNull FormatSelector<UndertowInvocationContext> formatSelector,
      final long responseTimeout) {
    super(
        service,
        serverProtocolFactory.newServerProtocol(c -> new Exchange(c.exchange), formatSelector),
        OperationFilterChains.defaultLocalFilterChains() // make configurable?
    );
    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;
  }

  @Contract(pure = true)
  public static @NotNull Function<UndertowInvocationContext, String> formatNameExtractor() {
    return c -> c.exchange.getRequestHeaders().getFirst(EpigraphHeaders.FORMAT);
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
    final OperationInvocationContext operationContext = newOperationInvocationContext(context);

    final HttpMethod method = getMethod(exchange);
    if (method == null) {
      writeInvocationErrorAndCloseContext(
          new OperationInvocationErrorImpl(
              HttpStatusCode.BAD_REQUEST, String.format("Unsupported HTTP method '%s'", exchange.getRequestMethod())
          ), context, operationContext
      );
    } else {
      try {
        handleRequest(getDecodedRequestString(exchange), method, getOperationName(exchange), context, operationContext);
      } catch (URISyntaxException e) {
        writeInvocationErrorAndCloseContext(
            new OperationInvocationErrorImpl(
                HttpStatusCode.BAD_REQUEST, String.format("Invalid URI syntax '%s'", e.getMessage())
            ), context, operationContext
        );
      }
    }
  }

  @Override
  protected long responseTimeout(final @NotNull UndertowInvocationContext context) { return responseTimeout; }

  @Override
  protected void close(final @NotNull EpigraphUndertowHandler.@NotNull UndertowInvocationContext context)
      throws IOException {
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
    final HeaderValues headerValues = exchange.getRequestHeaders().get(EpigraphHeaders.OPERATION_NAME);
    return headerValues == null ? null : headerValues.getFirst(); // warn if more than one?
  }

  class UndertowInvocationContext implements HttpInvocationContext {
    final @NotNull HttpServerExchange exchange;

    UndertowInvocationContext(@NotNull HttpServerExchange exchange) {
      this.exchange = exchange;
    }

    @Override
    public @NotNull Logger logger() { return LOG; }

    @Override
    public @NotNull TypesResolver typesResolver() { return typesResolver; }

    @Override
    public boolean isDebug() {
      final HeaderValues headerValues = exchange.getRequestHeaders().get(EpigraphHeaders.DEBUG_MODE);
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

  private static final class Exchange implements HttpExchange {
    private final @NotNull HttpServerExchange delegate;

    Exchange(final @NotNull HttpServerExchange delegate) {this.delegate = delegate;}

    @Override
    public @Nullable String getHeader(final @NotNull String headerName) {
      return delegate.getResponseHeaders().getFirst(headerName);
    }

    @Override
    public @NotNull InputStream getInputStream() { return delegate.getInputStream(); }

    @Override
    public void setStatusCode(final int statusCode) { delegate.setStatusCode(statusCode); }

    @Override
    public void setHeaders(final Map<String, String> headers) {
      for (final Map.Entry<String, String> entry : headers.entrySet()) {
        delegate.getResponseHeaders().add(HttpString.tryFromString(entry.getKey()), entry.getValue());
      }
    }

    @Override
    public @NotNull OutputStream getOutputStream() { return delegate.getOutputStream(); }

    @Override
    public void close() { delegate.endExchange(); }
  }

}