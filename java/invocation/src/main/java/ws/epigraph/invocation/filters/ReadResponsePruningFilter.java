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

package ws.epigraph.invocation.filters;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.data.pruning.ReqOutputRequiredDataPruner;
import ws.epigraph.invocation.*;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.util.HttpStatusCode;

import java.util.concurrent.CompletableFuture;

/**
 * Tries to keep `required` contract by pruning output data.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/required#request-projections">required flag for request projections</a>
 */
public class ReadResponsePruningFilter<Req extends OperationRequest, D extends Data, OD extends OperationDeclaration>
    extends AbstractOperationInvocationFilter<Req, ReadOperationResponse<D>, OD> {

  @SuppressWarnings("unchecked")
  @Override
  protected CompletableFuture<InvocationResult<ReadOperationResponse<D>>> invoke(
      final @NotNull OperationInvocation<Req, ReadOperationResponse<D>, OD> invocation,
      final @NotNull Req request,
      final @NotNull OperationInvocationContext context) {

    return invocation.invoke(request, context).thenApply(operationInvocationResult ->
        operationInvocationResult.apply(
            readOperationResponse -> {
              assert readOperationResponse != null;
              Data data = readOperationResponse.getData();
              if (data == null)
                return InvocationResult.success(readOperationResponse);

              ReqOutputRequiredDataPruner pruner = new ReqOutputRequiredDataPruner();
              final ReqOutputRequiredDataPruner.DataPruningResult pruningResult =
                  pruner.pruneData(data, request.outputProjection().entityProjection());

              if (pruningResult instanceof ReqOutputRequiredDataPruner.ReplaceData) {
                ReqOutputRequiredDataPruner.ReplaceData replaceData =
                    (ReqOutputRequiredDataPruner.ReplaceData) pruningResult;

                return InvocationResult.success(new ReadOperationResponse<>((D) replaceData.newData));
              } else if (pruningResult instanceof ReqOutputRequiredDataPruner.RemoveData) {
                return InvocationResult.success(new ReadOperationResponse<>((D) null));
              } else if (pruningResult instanceof ReqOutputRequiredDataPruner.Fail) {
                ReqOutputRequiredDataPruner.Fail fail = (ReqOutputRequiredDataPruner.Fail) pruningResult;
                final ReqOutputRequiredDataPruner.Reason reason = fail.reason;
                return InvocationResult.failure(
                    new InvocationErrorImpl(
                        reason.isOperationError ? HttpStatusCode.INTERNAL_OPERATION_ERROR
                                                : HttpStatusCode.PRECONDITION_FAILED, reason.toString()
                    )
                );
              } // else keep

              return InvocationResult.success(readOperationResponse);

            },

            InvocationResult::failure
        )
    );
  }
}
