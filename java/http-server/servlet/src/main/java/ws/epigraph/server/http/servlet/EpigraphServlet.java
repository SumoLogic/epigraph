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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ws.epigraph.data.Data;
import ws.epigraph.http.EpigraphHeaders;
import ws.epigraph.invocation.OperationFilterChains;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EpigraphServlet extends HttpServlet {
  public static final String RESPONSE_TIMEOUT_SERVLET_PARAMETER = "response_timeout";
  public static final long DEFAULT_RESPONSE_TIMEOUT = 1000;
  protected TypesResolver typesResolver;
  protected Logger logger;
  protected long responseTimeout;

  protected @NotNull TypesResolver initTypesResolver(ServletConfig config) { return StaticTypesResolver.instance(); }

  protected abstract @NotNull Service initService(ServletConfig config) throws ServiceInitializationException;

  protected @NotNull OperationFilterChains<? extends Data>
  initOperationFilterChains(ServletConfig config) { return OperationFilterChains.defaultFilterChains(); }

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

  protected abstract void handleRequest(HttpMethod method, HttpServletRequest req, HttpServletResponse resp);

  protected  @Nullable String getOperationName(@NotNull HttpServletRequest req) {
    return req.getHeader(EpigraphHeaders.OPERATION_NAME);
  }
}
