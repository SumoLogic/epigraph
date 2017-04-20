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

import com.fasterxml.jackson.core.JsonFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.AbstractHttpServer;
import ws.epigraph.server.http.HtmlCapableOperationInvocationError;
import ws.epigraph.server.http.InvocationContext;
import ws.epigraph.server.http.RequestHeaders;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.wire.FormatWriter;
import ws.epigraph.wire.OpInputFormatReader;
import ws.epigraph.wire.ReqInputFormatReader;
import ws.epigraph.wire.ReqUpdateFormatReader;
import ws.epigraph.wire.json.reader.OpInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqInputJsonFormatReader;
import ws.epigraph.wire.json.reader.ReqUpdateJsonFormatReader;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.LinkedBlockingQueue;

import static ws.epigraph.server.http.Constants.CONTENT_TYPE_HTML;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EpigraphServlet extends HttpServlet {
  private static final String URI_CHARSET = StandardCharsets.UTF_8.name();

  // no header constants for servlet API?
  public static final String ACCEPT_HEADER = "Accept";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";

  public static final String RESPONSE_TIMEOUT_SERVLET_PARAMETER = "response_timeout";
  public static final long DEFAULT_RESPONSE_TIMEOUT = 1000;

  private final @NotNull JsonFactory jsonFactory = new JsonFactory();
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
      server = new Server( initService(config), initOperationFilterChains(config) );

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

  protected @NotNull OperationFilterChains<? extends Data> initOperationFilterChains(ServletConfig config) {
    return OperationFilterChains.defaultFilterChains();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.GET, req); }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.POST, req); }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.PUT, req); }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) { handleRequest(HttpMethod.DELETE, req); }

  protected void handleRequest(HttpMethod method, HttpServletRequest req) {
    try {
      // could use `getPathInfo` here which gives decoded URI without path, but we still have to
      // decode query params, so it's better to use the same decoder for both

      final String uri = req.getRequestURI();
      final String path = req.getContextPath();
      final String queryString = req.getQueryString();

      // remove path
      final String uriNoPath = path == null || path.isEmpty() ? uri : uri.substring(path.length());

      // add query string and decode
      String fullUri = URLDecoder.decode(uriNoPath, URI_CHARSET);
      if (queryString != null)
        fullUri = fullUri + "?" + URLDecoder.decode(queryString, URI_CHARSET);

      final AsyncContext asyncContext = req.startAsync();
      asyncContext.setTimeout(responseTimeout);

      server.handleRequest(
          fullUri,
          method,
          getOperationName(req),
          new ServletInvocationContext(asyncContext)
      );
    } catch (UnsupportedEncodingException e) {
      // can never happen
      throw new RuntimeException(e);
    }
  }

  private @Nullable String getOperationName(@NotNull HttpServletRequest req) {
    return req.getHeader(RequestHeaders.OPERATION_NAME);
  }

  private class ServletInvocationContext extends InvocationContext {
    final @NotNull AsyncContext asyncContext;

    ServletInvocationContext(final @NotNull AsyncContext context) { asyncContext = context; }

    @NotNull ServletRequest request() { return asyncContext.getRequest(); }

    @NotNull ServletResponse response() { return asyncContext.getResponse(); }

    @Override
    public Logger logger() { return logger; }

    @Override
    public TypesResolver typesResolver() { return typesResolver; }
  }

  private class Server extends AbstractHttpServer<ServletInvocationContext> {
    protected Server(
        final @NotNull Service service,
        final @NotNull OperationFilterChains<? extends Data> invocations) {
      super(service, invocations);
    }

    @Override
    public void handleRequest(
        final @NotNull String decodedUri,
        final @NotNull HttpMethod requestMethod,
        final @Nullable String operationName,
        final @NotNull EpigraphServlet.ServletInvocationContext context) {
      super.handleRequest(decodedUri, requestMethod, operationName, context);
    }

    @Override
    protected long responseTimeout(final @NotNull ServletInvocationContext context) { return responseTimeout; }

    @Override
    protected OpInputFormatReader opInputReader(final @NotNull ServletInvocationContext context) throws IOException {
      return new OpInputJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected ReqInputFormatReader reqInputReader(final @NotNull ServletInvocationContext context) throws IOException {
      return new ReqInputJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected ReqUpdateFormatReader reqUpdateReader(final @NotNull ServletInvocationContext context)
        throws IOException {
      return new ReqUpdateJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected void writeFormatResponse(
        final int statusCode,
        final @NotNull ServletInvocationContext context,
        final @NotNull FormatResponseWriter formatWriter) {

      try {
        HttpServletResponse servletResponse = (HttpServletResponse) context.response();
        servletResponse.setStatus(statusCode);

        FormatWriter writer = new JsonFormatWriter(servletResponse.getOutputStream()); // todo make configurable
        servletResponse.setContentType(writer.httpContentType());
        servletResponse.setCharacterEncoding(writer.characterEncoding());

        formatWriter.write(writer);
        writer.close();
      } catch (IOException e) {
        context.logger().error("Error writing response", e);
      }
    }

    @Override
    protected void writeInvocationErrorResponse(
        final @NotNull OperationInvocationError error,
        final @NotNull ServletInvocationContext context) {

      HttpServletRequest servletRequest = (HttpServletRequest) context.request();
      HttpServletResponse servletResponse = (HttpServletResponse) context.response();
      servletResponse.setStatus(error.status().httpCode());

      try {
        if (error instanceof HtmlCapableOperationInvocationError && htmlAccepted(servletRequest)) {
          servletResponse.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_HTML);
          servletResponse.getWriter().write(((HtmlCapableOperationInvocationError) error).htmlMessage());
        } else {
          servletResponse.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_HTML);
          servletResponse.getWriter().write(error.message() + "\n");
        }
      } catch (IOException e) {
        context.logger().error("Error writing response", e);
      }
    }

    @Override
    protected void close(final @NotNull ServletInvocationContext context) throws IOException {
      context.response().flushBuffer();
      context.asyncContext.complete();
    }
  }

  private static boolean htmlAccepted(@NotNull HttpServletRequest request) {
    final Enumeration<String> enumeration = request.getHeaders(ACCEPT_HEADER);
    while (enumeration.hasMoreElements()) {
      String header = enumeration.nextElement();
      if (header.toLowerCase().contains(CONTENT_TYPE_HTML)) return true;
    }
    return false;
  }
}
