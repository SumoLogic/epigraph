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
import org.apache.http.client.methods.HttpPost;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.service.operations.CreateOperationRequest;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RemoteCreateOperationInvocation
    extends AbstractRemoteOperationInvocation<CreateOperationRequest, CreateOperationDeclaration> {

  protected RemoteCreateOperationInvocation(
      final @NotNull HttpHost host,
      final @NotNull HttpRequestDispatcher requestDispatcher,
      final @NotNull String resourceName,
      final @NotNull CreateOperationDeclaration operationDeclaration,
      final @NotNull ServerProtocol serverProtocol,
      final @NotNull Charset charset) {
    super(host, requestDispatcher, resourceName, operationDeclaration, serverProtocol, charset);
  }

  @Override
  protected HttpRequest composeHttpRequest(
      final @NotNull CreateOperationRequest operationRequest,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    ReqInputFieldProjection inputFieldProjection = operationRequest.inputProjection();

    String uri = UriComposer.composeCreateUri(
        resourceName,
        operationRequest.path(),
        inputFieldProjection,
        operationRequest.outputProjection()
    );

    System.out.println("uri = " + uri);

    HttpPost post = new HttpPost(uri);
    post.setEntity(serverProtocol.createRequestEntity(
        inputFieldProjection == null ? null : inputFieldProjection.varProjection(),
        operationDeclaration.inputProjection().varProjection(),
        operationInvocationContext
    ));

    return post;
  }

}
