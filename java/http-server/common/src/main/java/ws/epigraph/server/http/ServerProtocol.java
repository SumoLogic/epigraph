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

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.InvocationError;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.ReqModelProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.schema.operations.OperationKind;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface ServerProtocol<C extends HttpInvocationContext> {
  // server-side counterpart of ws.epigraph.client.http.ServerProtocol

  Data readInput(
      @NotNull OpOutputVarProjection opInputProjection,
      @Nullable ReqEntityProjection reqInputProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws IOException;

  Data readUpdateInput(
      @NotNull OpOutputVarProjection opInputProjection,
      @Nullable ReqEntityProjection reqUpdateProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws IOException;

  void writeDataResponse(
      @NotNull OperationKind operationKind,
      @NotNull ReqEntityProjection projection,
      @Nullable Data data,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext);

  void writeDatumResponse(
      @NotNull OperationKind operationKind,
      @NotNull ReqModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext);

  void writeErrorResponse(
      @NotNull OperationKind operationKind,
      @NotNull ErrorValue error,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext);

  void writeEmptyResponse(
      @NotNull OperationKind operationKind,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext);

  void writeInvocationErrorResponse(
      @NotNull InvocationError error,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext);

}
