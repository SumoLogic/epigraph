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

package ws.epigraph.client.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SyncHttpRequestDispatcher implements HttpRequestDispatcher {
  private static final Logger LOG = LoggerFactory.getLogger(SyncHttpRequestDispatcher.class);

  private final @NotNull CloseableHttpClient httpClient;

  public SyncHttpRequestDispatcher() {
    this(HttpClients.createDefault());
  }

  public SyncHttpRequestDispatcher(final @NotNull CloseableHttpClient client) {
    httpClient = client;
  }

  @Override
  public <T> CompletableFuture<T> runRequest(
      final @NotNull HttpHost target,
      final @NotNull HttpRequest request,
      final @NotNull Function<HttpResponse, T> resultProcessor) {

    CompletableFuture<T> f;

    try {
      CloseableHttpResponse response = httpClient.execute(target, request);
      f = CompletableFuture.completedFuture(resultProcessor.apply(response));
      try {
        response.close();
      } catch (IOException e) {
        LOG.error("Error closing HTTP response", e);
      }
    } catch (IOException e) {
      f = new CompletableFuture<>();
      f.completeExceptionally(e);
    }

    return f;
  }

  @Override
  public void shutdown() throws IOException { httpClient.close(); }
}
