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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.nio.client.HttpAsyncClient;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.service.operations.DeleteOperationRequest;
import ws.epigraph.util.HttpStatusCode;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RemoteDeleteOperationInvocation
    extends AbstractRemoteOperationInvocation<DeleteOperationRequest, DeleteOperationDeclaration> {

  public RemoteDeleteOperationInvocation(
      final @NotNull HttpHost host,
      final @NotNull HttpAsyncClient httpClient,
      final @NotNull String resourceName,
      final @NotNull DeleteOperationDeclaration operationDeclaration,
      final @NotNull ServerProtocol serverProtocol,
      final @NotNull Charset charset) {

    super(host, httpClient, resourceName, operationDeclaration, serverProtocol, charset, HttpStatusCode.OK);
  }

  @Override
  protected HttpRequest composeHttpRequest(
      final @NotNull DeleteOperationRequest operationRequest,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    ReqFieldProjection deleteFieldProjection = operationRequest.deleteProjection();

    String uri = UriComposer.composeDeleteUri(
        resourceName,
        operationRequest.path(),
        deleteFieldProjection,
        operationRequest.outputProjection()
    );

    return new HttpDelete(uri);
  }

}
