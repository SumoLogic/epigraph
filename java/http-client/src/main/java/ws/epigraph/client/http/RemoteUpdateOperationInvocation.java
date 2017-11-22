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
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.service.operations.UpdateOperationRequest;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeApi;
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

    String uri = UriComposer.composeUpdateUri(
        resourceName,
        operationRequest.path(),
        operationRequest.updateStepsAndProjection(),
        operationRequest.outputStepsAndProjection()
    );

    return new HttpPut(uri);
  }

  @Override
  protected @Nullable HttpContentProducer requestContentProducer(
      @NotNull UpdateOperationRequest request, @NotNull OperationInvocationContext operationInvocationContext) {

    // nullable here is legit but breaks JaCoCo: http://forge.ow2.org/tracker/?func=detail&aid=317789&group_id=23&atid=100023
    /*@Nullable*/
    StepsAndProjection<ReqFieldProjection> updateStepsAndProjection = request.updateStepsAndProjection();
    Data data = request.data();

    Type dataType = data.type();
    TypeApi projectionType = updateStepsAndProjection == null
                             ? operationDeclaration.inputProjection().projection().type()
                             : updateStepsAndProjection.projection().projection().type();

    if (!projectionType.isAssignableFrom(dataType)) {
      throw new IllegalArgumentException(
          "Update projection type " + projectionType.name() + " is not assignable from data type " + dataType.name());
    }

    return serverProtocol.updateRequestContentProducer(
        StepsAndProjection.unwrapNullable(updateStepsAndProjection, AbstractFieldProjection::projection),
        operationDeclaration.inputProjection().projection(),
        data,
        operationInvocationContext
    );
  }

}
