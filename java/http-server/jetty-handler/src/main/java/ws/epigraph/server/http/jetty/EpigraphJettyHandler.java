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

package ws.epigraph.server.http.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.*;
import ws.epigraph.server.http.servlet.ServletExchange;
import ws.epigraph.service.Service;
import ws.epigraph.wire.json.reader.OpInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphJettyHandler extends AbstractHandler {
  public static final Logger LOG = LoggerFactory.getLogger(EpigraphJettyHandler.class);

  private final @NotNull TypesResolver typesResolver;
  private final int responseTimeout;
  private final @NotNull Server server;

  public EpigraphJettyHandler(
      final @NotNull Service service,
      final @NotNull FormatBasedServerProtocol.Factory<JettyHandlerInvocationContext> serverProtocolFactory,
      final @NotNull OperationFilterChains<? extends Data> filterChains,
      final @NotNull TypesResolver typesResolver,
      final int responseTimeout) {

    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;

    server = new Server(service, serverProtocolFactory, filterChains);
  }

  @Override
  public void handle(
      final String target,
      final Request baseRequest,
      final HttpServletRequest request,
      final HttpServletResponse response)
      throws IOException, ServletException {

    final HttpMethod method = HttpMethod.fromString(request.getMethod());

    if (method == null) {
      response.setStatus(400);
      response.getWriter().write("Invalid HTTP method '" + request.getMethod() + "'");
    } else {
      try {
        final String decodedUri =
            Util.decodeUri(baseRequest.getRequestURI(), request.getContextPath(), request.getServletPath());

        final AsyncContext asyncContext = request.startAsync();
        if (responseTimeout > 0)
          asyncContext.setTimeout(responseTimeout);

        server.handleRequest(
            decodedUri,
            method,
            getOperationName(baseRequest),
            new JettyHandlerInvocationContext(asyncContext)
        );

      } catch (URISyntaxException e) {
        response.setStatus(400);
        response.getWriter().write("Invalid URI syntax '" + e.getMessage() + "'");
      }
    }

  }

  private @Nullable String getOperationName(@NotNull HttpServletRequest req) {
    return req.getHeader(RequestHeaders.OPERATION_NAME);
  }

  private final class JettyHandlerInvocationContext implements HttpInvocationContext {
    final @NotNull AsyncContext asyncContext;

    JettyHandlerInvocationContext(final @NotNull AsyncContext context) {
      asyncContext = context;
    }

    @Override
    public @NotNull Logger logger() { return LOG; }

    @Override
    public @NotNull TypesResolver typesResolver() { return typesResolver; }
  }

  private class Server extends AbstractHttpServer<JettyHandlerInvocationContext> {
    protected Server(
        @NotNull Service service,
        @NotNull FormatBasedServerProtocol.Factory<JettyHandlerInvocationContext> serverProtocolFactory,
        @NotNull OperationFilterChains<? extends Data> invocations) {
      super(
          service,
          serverProtocolFactory.newServerProtocol(
              c -> new ServletExchange(c.asyncContext),
              // todo change format based on request (c). Pass format name -> format readers/writers map to ctor?
              c -> new OpInputJsonFormatReader.Factory(),
              c -> new ReqInputJsonFormatReader.Factory(),
              c -> new ReqUpdateJsonFormatReader.Factory(),
              c -> new JsonFormatWriter.JsonFormatWriterFactory()
          ),
          invocations
      );
    }

    public void handleRequest(
        final @NotNull String decodedUri,
        final @NotNull HttpMethod requestMethod,
        final @Nullable String operationName,
        final @NotNull JettyHandlerInvocationContext context) {
      super.handleRequest(decodedUri, requestMethod, operationName, context, newOperationInvocationContext(context));
    }

    @Override
    protected long responseTimeout(final @NotNull JettyHandlerInvocationContext context) { return responseTimeout; }

    @Override
    protected void close(final @NotNull JettyHandlerInvocationContext context) throws IOException {
      context.asyncContext.getResponse().flushBuffer();
      context.asyncContext.complete();
    }
  }

}
