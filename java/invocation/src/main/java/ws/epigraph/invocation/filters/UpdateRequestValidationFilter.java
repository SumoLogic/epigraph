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
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationFilter;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.service.operations.OperationResponse;
import ws.epigraph.service.operations.UpdateOperationRequest;
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.data.validation.OpInputDataValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Checks that all parts marked as `required` by operation input projection are actually present
 * in request data
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateRequestValidationFilter<Rsp extends OperationResponse>
    implements OperationInvocationFilter<UpdateOperationRequest, Rsp> {

  private final @NotNull UpdateOperationDeclaration operationDeclaration;

  public UpdateRequestValidationFilter(final @NotNull UpdateOperationDeclaration declaration) {
    operationDeclaration = declaration;
  }

  @Override
  public OperationInvocation<UpdateOperationRequest, Rsp>
  apply(final OperationInvocation<UpdateOperationRequest, Rsp> invocation) {
    return (request, context) -> {
      OpInputDataValidator validator = new OpInputDataValidator();
      validator.validateData(request.data(), operationDeclaration.inputProjection().varProjection());
      List<? extends DataValidationError> errors = validator.errors();

      return errors.isEmpty()
             ? invocation.invoke(request, context)
             : CompletableFuture.completedFuture(OperationInvocationResult.failure(FilterUtil.validationError(errors)));
    };

  }
}
