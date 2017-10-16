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

package ws.epigraph.server.http.springhandler;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import ws.epigraph.http.EpigraphHeaders;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.server.http.*;
import ws.epigraph.server.http.servlet.SyncServletExchange;
import ws.epigraph.service.Service;
import ws.epigraph.util.HttpStatusCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SyncHttpRequestHandler
    extends AbstractHttpServer<SyncHttpRequestHandler.HttpRequestHandlerInvocationContext>
    implements HttpRequestHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final long responseTimeout;

  public SyncHttpRequestHandler(Service service, long responseTimeout) {
    super(
        service,
        new FormatBasedServerProtocol.Factory<HttpRequestHandlerInvocationContext>().newServerProtocol(
            c -> new SyncServletExchange(c.request, c.response),
            DefaultFormats.instance(formatNameExtractor()),
            StaticTypesResolver.instance()
        ),
        OperationFilterChains.defaultFilterChains()
    );
    this.responseTimeout = responseTimeout;
  }

  @Override
  public void handleRequest(
      final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

    try {
      final String decodedUri = Util.decodeUri(req.getRequestURI(), req.getContextPath(), req.getServletPath());

      HttpRequestHandlerInvocationContext context = new HttpRequestHandlerInvocationContext(req, resp);

      HttpMethod method = HttpMethod.fromString(req.getMethod());
      if (method == null) {
        resp.setStatus(HttpStatusCode.BAD_REQUEST);
        try {
          resp.getWriter().write("Invalid HTTP method '" + req.getMethod() + "'");
        } catch (IOException ioe) {
          log.error("Error getting output writer", ioe);
        }
      } else {
        handleRequest(
            decodedUri,
            method,
            getOperationName(req),
            context,
            newOperationInvocationContext(context)
        );
      }
    } catch (URISyntaxException e) {
      resp.setStatus(400);
      try {
        resp.getWriter().write("Invalid URI syntax '" + e.getMessage() + "'");
      } catch (IOException ioe) {
        log.error("Error getting output writer", ioe);
      }
    }
  }

  protected @Nullable String getOperationName(@NotNull HttpServletRequest req) {
    return req.getHeader(EpigraphHeaders.OPERATION_NAME);
  }

  @Override
  protected long responseTimeout(@NotNull SyncHttpRequestHandler.HttpRequestHandlerInvocationContext context) {
    return responseTimeout;
  }

  @Contract(pure = true)
  public static @NotNull Function<HttpRequestHandlerInvocationContext, String> formatNameExtractor() {
    return c -> c.request.getHeader(EpigraphHeaders.FORMAT);
  }

  protected class HttpRequestHandlerInvocationContext implements HttpInvocationContext {
    final HttpServletRequest request;
    final HttpServletResponse response;

    public HttpRequestHandlerInvocationContext(
        final HttpServletRequest request,
        final HttpServletResponse response) {
      this.request = request;
      this.response = response;
    }

    @Override
    public @NotNull Logger logger() { return log; }

    @Override
    public @NotNull TypesResolver typesResolver() {
      return StaticTypesResolver.instance();
    }
  }
}
