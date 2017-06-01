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
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.data.validation.OpInputDataValidator;
import ws.epigraph.invocation.AbstractOperationInvocationFilter;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.InvocationResult;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.service.operations.CreateOperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Checks that all parts marked as `required` by operation input projection are actually present
 * in request data
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateRequestValidationFilter<Rsp extends OperationResponse>
    extends AbstractOperationInvocationFilter<CreateOperationRequest, Rsp, CreateOperationDeclaration> {

  @Override
  protected CompletableFuture<InvocationResult<Rsp>> invoke(
      final @NotNull OperationInvocation<CreateOperationRequest, Rsp, CreateOperationDeclaration> invocation,
      final @NotNull CreateOperationRequest request,
      final @NotNull OperationInvocationContext context) {

    OpInputDataValidator validator = new OpInputDataValidator();
    validator.validateData(request.data(), invocation.operationDeclaration().inputProjection().varProjection());
    List<? extends DataValidationError> errors = validator.errors();

    return errors.isEmpty()
           ? invocation.invoke(request, context)
           : CompletableFuture.completedFuture(InvocationResult.failure(FilterUtil.validationError(errors)));
  }
}
