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

import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationFilter;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.validation.data.DataValidationError;
import ws.epigraph.validation.data.ReqOutputDataValidator;

import java.util.Collection;

/**
 * Ensures that all bits required by the output projection are actually present
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadResponseValidationFilter<Req extends OperationRequest, D extends Data>
    implements OperationInvocationFilter<Req, ReadOperationResponse<D>> {

  @Override
  public OperationInvocation<Req, ReadOperationResponse<D>>
  apply(final OperationInvocation<Req, ReadOperationResponse<D>> invocation) {

    return request -> invocation.invoke(request).thenApply(result ->
        result.apply(
            response -> {
              Data data = response.getData();
              if (data == null)
                return OperationInvocationResult.success(response);

              ReqOutputDataValidator validator = new ReqOutputDataValidator();
              validator.validateData(data, request.outputProjection().varProjection());

              Collection<? extends DataValidationError> validationErrors = validator.errors();
              return validationErrors.isEmpty()
                     ? OperationInvocationResult.success(response)
                     : OperationInvocationResult.failure(FilterUtil.validationError(validationErrors));

            },

            OperationInvocationResult::failure
        )
    );
  }
}
