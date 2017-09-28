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
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.data.validation.ReqOutputDataValidator;
import ws.epigraph.invocation.AbstractOperationInvocationFilter;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.InvocationResult;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Ensures that all bits required by the output projection are actually present
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadResponseValidationFilter<Req extends OperationRequest, D extends Data>
    extends AbstractOperationInvocationFilter<Req, ReadOperationResponse<D>, ReadOperationDeclaration> {

  @Override
  protected CompletableFuture<InvocationResult<ReadOperationResponse<D>>> invoke(
      final @NotNull OperationInvocation<Req, ReadOperationResponse<D>, ReadOperationDeclaration> invocation,
      final @NotNull Req request,
      final @NotNull OperationInvocationContext context) {

    return invocation.invoke(request, context).thenApply(result ->
        result.apply(
            response -> {
              Data data = response.getData();
              if (data == null)
                return InvocationResult.success(response);

              ReqOutputDataValidator validator = new ReqOutputDataValidator();
              validator.validateData(data, request.outputProjection().entityProjection());

              Collection<? extends DataValidationError> validationErrors = validator.errors();
              return validationErrors.isEmpty()
                     ? InvocationResult.success(response)
                     : InvocationResult.failure(FilterUtil.validationError(validationErrors));

            },

            InvocationResult::failure
        )
    );
  }
}
