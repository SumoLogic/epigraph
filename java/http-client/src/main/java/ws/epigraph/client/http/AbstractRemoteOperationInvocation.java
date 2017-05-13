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

import org.apache.http.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.http.EpigraphHeaders;
import ws.epigraph.http.Headers;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractRemoteOperationInvocation<Req extends OperationRequest, OD extends OperationDeclaration>
    implements OperationInvocation<Req, ReadOperationResponse<?>> {

  protected final @NotNull HttpHost host;
  protected final @NotNull HttpAsyncClient httpClient;
  protected final @NotNull String resourceName;
  protected final @NotNull OD operationDeclaration;
  protected final @NotNull ServerProtocol serverProtocol;
  protected final @NotNull Charset charset;

  protected AbstractRemoteOperationInvocation(
      final @NotNull HttpHost host,
      final @NotNull HttpAsyncClient httpClient,
      final @NotNull String resourceName,
      final @NotNull OD operationDeclaration,
      final @NotNull ServerProtocol serverProtocol,
      final @NotNull Charset charset) {

    this.host = host;
    this.httpClient = httpClient;
    this.resourceName = resourceName;
    this.operationDeclaration = operationDeclaration;
    this.serverProtocol = serverProtocol;
    this.charset = charset;
  }

  @Override
  public CompletableFuture<OperationInvocationResult<ReadOperationResponse<?>>> invoke(
      @NotNull Req request, @NotNull OperationInvocationContext context) {

    HttpRequest httpRequest = composeHttpRequest(request, context);

    // set up operation name header to disambiguate routing
    String operationName = operationDeclaration.name();
    httpRequest.addHeader(
        EpigraphHeaders.OPERATION_NAME,
        operationName == null ? ReadOperationDeclaration.DEFAULT_NAME : operationName
    );

    for (String mimeType : serverProtocol.mimeTypes())
      httpRequest.addHeader(HttpHeaders.ACCEPT, mimeType);

    httpRequest.addHeader(HttpHeaders.ACCEPT_CHARSET, charset.name());

    final HttpAsyncRequestProducer requestProducer;
    @Nullable ContentProducer contentProducer = requestContentProducer(request, context);

    if (contentProducer == null)
      requestProducer = new BasicAsyncRequestProducer(host, httpRequest);
    else {
      if (!(httpRequest instanceof HttpEntityEnclosingRequest))
        throw new IllegalStateException(String.format(
            "Operation %s/%s remote invocation error: request is not an HttpEntityEnclosingRequest (%s), while contentProducer is non null",
            resourceName,
            operationDeclaration.nameOrDefaultName(),
            httpRequest.getClass().getName()
        ));

      httpRequest.addHeader(Headers.CONTENT_TYPE, contentProducer.contentType().toString());
      requestProducer =
          new RequestProducer(host, (HttpEntityEnclosingRequest) httpRequest, contentProducer.httpContentProducer());
    }

    CompletableFuture<OperationInvocationResult<ReadOperationResponse<?>>> f = new CompletableFuture<>();

    httpClient.execute(
        requestProducer,

        // NB this consumer does in-memory buffering of the respose and currently there's no way around it
        // see 'true async support for http' in `todo.md`
        new BasicAsyncResponseConsumer(),

        new FutureCallback<HttpResponse>() {
          @Override
          public void completed(final HttpResponse result) {
            f.complete(
                serverProtocol.readResponse(
                    request.outputProjection().varProjection(),
                    context,
                    result
                )
            );
          }

          @Override
          public void failed(final Exception ex) {
            f.completeExceptionally(ex);
          }

          @Override
          public void cancelled() {
            f.cancel(false);
          }
        }
    );

    return f;

  }

  protected abstract HttpRequest composeHttpRequest(
      @NotNull Req operationRequest,
      @NotNull OperationInvocationContext operationInvocationContext);

  protected @Nullable ContentProducer requestContentProducer(
      @NotNull Req request,
      @NotNull OperationInvocationContext context) { return null; }

  private static class RequestProducer extends BasicAsyncRequestProducer {
    RequestProducer(
        final HttpHost target,
        final HttpEntityEnclosingRequest request,
        final HttpAsyncContentProducer producer) {

      super(target, request, producer);
    }
  }

}
