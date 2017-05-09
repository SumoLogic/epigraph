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

import net.jcip.annotations.ThreadSafe;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@ThreadSafe
public class AsyncHttpRequestDispatcher implements HttpRequestDispatcher {
  public static final String HTTP_AGENT = "Epigraph/0.1";
  private static final Logger LOG = LoggerFactory.getLogger(AsyncHttpRequestDispatcher.class);

  private final @NotNull ConnectingIOReactor reactor;
  private final @NotNull BasicNIOConnPool connectionPool;
  private final @NotNull HttpAsyncRequester requester;
  private final long shutdownTimeout;

  public AsyncHttpRequestDispatcher(
      @NotNull ConnectionConfig connectionConfig,
      @NotNull IOReactorConfig reactorConfig,
      int maxConnections,
      final long shutdownTimeout) throws IOReactorException {

    this.shutdownTimeout = shutdownTimeout;

    HttpAsyncRequestExecutor protocolHandler = new HttpAsyncRequestExecutor();
    IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(protocolHandler, connectionConfig);

    reactor = new DefaultConnectingIOReactor(reactorConfig);

    new Thread(null, () -> {
      try {
        reactor.execute(ioEventDispatch);
      } catch (IOException e) {
        LOG.error("I/O error", e);
      }
    }, "I/O reactor thread").start();

    connectionPool = new BasicNIOConnPool(reactor, connectionConfig);
    connectionPool.setDefaultMaxPerRoute(maxConnections);
    connectionPool.setMaxTotal(maxConnections);

    HttpProcessor httpProcessor = HttpProcessorBuilder.create()
        .add(new RequestContent())
        .add(new RequestTargetHost())
        .add(new RequestConnControl())
        .add(new RequestUserAgent(HTTP_AGENT))
        .add(new RequestExpectContinue(true)).build();

    requester = new HttpAsyncRequester(httpProcessor);
  }

  @Override
  public <T> @NotNull CompletableFuture<T> runRequest(
      @NotNull HttpHost target,
      @NotNull HttpRequest request,
      @NotNull Function<HttpResponse, T> resultProcessor) {

    final CompletableFuture<T> f = new CompletableFuture<>();

    requester.execute(
        new BasicAsyncRequestProducer(target, request),
        new BasicAsyncResponseConsumer(),
        connectionPool,
        HttpCoreContext.create(),
        new FutureCallback<HttpResponse>() {
          @Override
          public void completed(final HttpResponse result) {

            f.complete(resultProcessor.apply(result));

            if (result instanceof Closeable) {
              Closeable closeableHttpResponse = (Closeable) result;
              try {
                closeableHttpResponse.close();
              } catch (IOException e) {
                LOG.error("Error closing HTTP response", e);
              }
            }

          }

          @Override
          public void failed(final Exception ex) { f.completeExceptionally(ex); }

          @Override
          public void cancelled() { f.cancel(false); }
        }
    );

    return f;
  }

  @Override
  public void shutdown() throws IOException {
    reactor.shutdown(shutdownTimeout);
    connectionPool.shutdown(shutdownTimeout);
  }
}
