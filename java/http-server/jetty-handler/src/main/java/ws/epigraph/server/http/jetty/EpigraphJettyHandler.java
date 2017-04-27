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

import com.fasterxml.jackson.core.JsonFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.*;
import ws.epigraph.service.Service;
import ws.epigraph.wire.FormatWriter;
import ws.epigraph.wire.OpInputFormatReader;
import ws.epigraph.wire.ReqInputFormatReader;
import ws.epigraph.wire.ReqUpdateFormatReader;
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
import java.util.Enumeration;

import static ws.epigraph.server.http.Constants.CONTENT_TYPE_HTML;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphJettyHandler extends AbstractHandler {
  public static final Logger LOG = LoggerFactory.getLogger(EpigraphJettyHandler.class);

  // no header constants for servlet API?
  public static final String ACCEPT_HEADER = "Accept";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";

  private final @NotNull JsonFactory jsonFactory = new JsonFactory();

  private final @NotNull TypesResolver typesResolver;
  private final int responseTimeout;
  private final @NotNull Server server;

  public EpigraphJettyHandler(
      final @NotNull Service service,
      final @NotNull OperationFilterChains<? extends Data> filterChains,
      final @NotNull TypesResolver typesResolver,
      final int responseTimeout) {

    this.typesResolver = typesResolver;
    this.responseTimeout = responseTimeout;

    server = new Server(service, filterChains);
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

  private final class JettyHandlerInvocationContext extends InvocationContext {
    final @NotNull AsyncContext asyncContext;

    JettyHandlerInvocationContext(final @NotNull AsyncContext context) {
      asyncContext = context;
    }

    @Override
    public Logger logger() { return LOG; }

    @Override
    public TypesResolver typesResolver() { return typesResolver; }

    @NotNull HttpServletRequest request() { return (HttpServletRequest) asyncContext.getRequest(); }

    @NotNull HttpServletResponse response() { return (HttpServletResponse) asyncContext.getResponse(); }
  }

  private class Server extends AbstractHttpServer<JettyHandlerInvocationContext> {
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
        final @NotNull JettyHandlerInvocationContext context) {
      super.handleRequest(decodedUri, requestMethod, operationName, context);
    }

    @Override
    protected long responseTimeout(final @NotNull JettyHandlerInvocationContext context) { return responseTimeout; }

    @Override
    protected OpInputFormatReader opInputReader(@NotNull JettyHandlerInvocationContext context) throws IOException {
      return new OpInputJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected ReqInputFormatReader reqInputReader(@NotNull JettyHandlerInvocationContext context) throws IOException {
      return new ReqInputJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected ReqUpdateFormatReader reqUpdateReader(@NotNull JettyHandlerInvocationContext context) throws IOException {
      return new ReqUpdateJsonFormatReader(jsonFactory.createParser(context.request().getInputStream()));
    }

    @Override
    protected void writeFormatResponse(
        final int statusCode,
        final @NotNull JettyHandlerInvocationContext context,
        final @NotNull FormatResponseWriter formatWriter) {

      try {
        HttpServletResponse servletResponse = context.response();
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
        final @NotNull JettyHandlerInvocationContext context) {

      HttpServletRequest servletRequest = context.request();
      HttpServletResponse servletResponse = context.response();
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
    protected void close(final @NotNull JettyHandlerInvocationContext context) throws IOException {
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
