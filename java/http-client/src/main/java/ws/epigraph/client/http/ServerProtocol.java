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

import org.apache.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.service.operations.ReadOperationResponse;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface ServerProtocol {
  // client-side counterpart of ws.epigraph.server.http.ServerProtocol

  /**
   * @return collection of mime types accepted by this protocol
   */
  @NotNull String[] mimeTypes();

  OperationInvocationResult<ReadOperationResponse<?>> readResponse(
      @NotNull ReqOutputVarProjection projection,
      @NotNull OperationInvocationContext operationInvocationContext,
      @NotNull HttpResponse httpResponse,
      int okStatusCode);

  @NotNull HttpContentProducer createRequestContentProducer(
      @Nullable ReqInputVarProjection reqInputProjection,
      @NotNull OpInputVarProjection opInputProjection,
      @NotNull Data inputData,
      @NotNull OperationInvocationContext operationInvocationContext);

  @NotNull HttpContentProducer updateRequestContentProducer(
      @Nullable ReqUpdateVarProjection reqInputProjection,
      @NotNull OpInputVarProjection opInputProjection,
      @NotNull Data inputData,
      @NotNull OperationInvocationContext operationInvocationContext);
}
