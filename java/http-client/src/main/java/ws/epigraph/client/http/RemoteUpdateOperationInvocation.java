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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.nio.client.HttpAsyncClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.service.operations.UpdateOperationRequest;
import ws.epigraph.util.HttpStatusCode;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RemoteUpdateOperationInvocation
    extends AbstractRemoteOperationInvocation<UpdateOperationRequest, UpdateOperationDeclaration> {

  public RemoteUpdateOperationInvocation(
      final @NotNull HttpHost host,
      final @NotNull HttpAsyncClient httpClient,
      final @NotNull String resourceName,
      final @NotNull UpdateOperationDeclaration operationDeclaration,
      final @NotNull ServerProtocol serverProtocol,
      final @NotNull Charset charset) {
    super(host, httpClient, resourceName, operationDeclaration, serverProtocol, charset, HttpStatusCode.OK);
  }

  @Override
  protected HttpRequest composeHttpRequest(
      final @NotNull UpdateOperationRequest operationRequest,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    ReqUpdateFieldProjection inputFieldProjection = operationRequest.updateProjection();

    String uri = UriComposer.composeUpdateUri(
        resourceName,
        operationRequest.path(),
        inputFieldProjection,
        operationRequest.outputProjection()
    );

    return new HttpPut(uri);
  }

  @Override
  protected @Nullable HttpContentProducer requestContentProducer(
      @NotNull UpdateOperationRequest request, @NotNull OperationInvocationContext operationInvocationContext) {

    ReqUpdateFieldProjection inputFieldProjection = request.updateProjection();

    return serverProtocol.updateRequestContentProducer(
        inputFieldProjection == null ? null : inputFieldProjection.varProjection(),
        operationDeclaration.inputProjection().varProjection(),
        request.data(),
        operationInvocationContext
    );
  }

}
