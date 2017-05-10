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

package ws.epigraph.server.http.servlet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ws.epigraph.data.Data;
import ws.epigraph.http.RequestHeaders;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.*;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Generic Servlet-based Epigraph endpoint implementation.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EpigraphServlet extends HttpServlet {
  public static final String RESPONSE_TIMEOUT_SERVLET_PARAMETER = "response_timeout";
  public static final long DEFAULT_RESPONSE_TIMEOUT = 1000;

  private TypesResolver typesResolver;
  private Logger logger;
  private Server server;
  private long responseTimeout;

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    logger = new ServletLogger(getServletName(), new LinkedBlockingQueue<>(), true, getServletContext());
    typesResolver = initTypesResolver(config);

    try {
      server = new Server(initService(config),
          new FormatBasedServerProtocol.Factory<>(),
          DefaultFormats.instance(formatNameExtractor()),
          initOperationFilterChains(config)
      );

      String responseTimeoutParameter = config.getInitParameter(RESPONSE_TIMEOUT_SERVLET_PARAMETER);
      responseTimeout = responseTimeoutParameter == null
                        ? DEFAULT_RESPONSE_TIMEOUT
                        : Long.parseLong(responseTimeoutParameter);

    } catch (ServiceInitializationException e) {
      throw new ServletException(e);
    }
  }

  protected @NotNull TypesResolver initTypesResolver(ServletConfig config) { return IndexBasedTypesResolver.INSTANCE; }

  protected abstract @NotNull Service initService(ServletConfig config) throws ServiceInitializationException;

  protected @NotNull OperationFilterChains<? extends Data>
  initOperationFilterChains(ServletConfig config) { return OperationFilterChains.defaultLocalFilterChains(); }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.GET, req, resp); }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.POST, req, resp); }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.PUT, req, resp); }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    handleRequest(HttpMethod.DELETE, req, resp);
  }

  protected void handleRequest(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) {
    try {
      final String decodedUri = Util.decodeUri(req.getRequestURI(), req.getContextPath(), req.getServletPath());

      final AsyncContext asyncContext = req.startAsync();
      asyncContext.setTimeout(responseTimeout);

      ServletInvocationContext context = new ServletInvocationContext(asyncContext);

      server.handleRequest(
          decodedUri,
          method,
          getOperationName(req),
          context
      );
    } catch (URISyntaxException e) {
      resp.setStatus(400);
      try {
        resp.getWriter().write("Invalid URI syntax '" + e.getMessage() + "'");
      } catch (IOException ioe) {
        log("Error getting output writer", ioe);
      }
    }
  }

  @Contract(pure = true)
  public static @NotNull Function<ServletInvocationContext, String> formatNameExtractor() {
    return c -> ((HttpServletRequest) c.asyncContext.getRequest()).getHeader(RequestHeaders.FORMAT);
  }

  private @Nullable String getOperationName(@NotNull HttpServletRequest req) {
    return req.getHeader(RequestHeaders.OPERATION_NAME);
  }

  private class ServletInvocationContext implements HttpInvocationContext {
    final @NotNull AsyncContext asyncContext;

    ServletInvocationContext(final @NotNull AsyncContext context) { asyncContext = context; }

    @Override
    public @NotNull Logger logger() { return logger; }

    @Override
    public @NotNull TypesResolver typesResolver() { return typesResolver; }
  }

  private class Server extends AbstractHttpServer<ServletInvocationContext> {
    protected Server(
        @NotNull Service service,
        @NotNull FormatBasedServerProtocol.Factory<ServletInvocationContext> serverProtocolFactory,
        @NotNull FormatSelector<ServletInvocationContext> formatSelector,
        @NotNull OperationFilterChains<? extends Data> filterChains) {
      super(
          service,
          serverProtocolFactory.newServerProtocol(c -> new ServletExchange(c.asyncContext), formatSelector),
          filterChains
      );
    }

    protected void handleRequest(
        final @NotNull String decodedUri,
        final @NotNull HttpMethod requestMethod,
        final @Nullable String operationName,
        final @NotNull EpigraphServlet.@NotNull ServletInvocationContext context) {
      super.handleRequest(decodedUri, requestMethod, operationName, context, newOperationInvocationContext(context));
    }

    @Override
    protected long responseTimeout(final @NotNull ServletInvocationContext context) { return responseTimeout; }

    @Override
    protected void close(final @NotNull ServletInvocationContext context) throws IOException {
      context.asyncContext.getResponse().flushBuffer();
      context.asyncContext.complete();
    }
  }

}
