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
import ws.epigraph.server.http.HttpExchange;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ServletExchange implements HttpExchange {
  private final @NotNull AsyncContext ac;

  public ServletExchange(final @NotNull AsyncContext ac) {this.ac = ac;}

  private @NotNull HttpServletRequest request() { return (HttpServletRequest) ac.getRequest();}

  private @NotNull HttpServletResponse response() { return (HttpServletResponse) ac.getResponse();}

  @Override
  public @Nullable String getHeader(final @NotNull String headerName) {
    return request().getHeader(headerName);
  }

  @Override
  public @NotNull InputStream getInputStream() throws IOException {
    return request().getInputStream();
  }

  @Override
  public void setStatusCode(final int statusCode) {
    response().setStatus(statusCode);
  }

  @Override
  public void setHeaders(final Map<String, String> headers) {
    for (final Map.Entry<String, String> entry : headers.entrySet()) {
      response().setHeader(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public @NotNull OutputStream getOutputStream() throws IOException {
    return response().getOutputStream();
  }

  @Override
  public void close() {
    ac.complete();
  }
}
