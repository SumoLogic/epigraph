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
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationFilter;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.schema.operations.CustomOperationDeclaration;
import ws.epigraph.service.operations.CustomOperationRequest;
import ws.epigraph.service.operations.OperationResponse;
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
public class CustomRequestValidationFilter<Rsp extends OperationResponse>
    implements OperationInvocationFilter<CustomOperationRequest, Rsp> {

  private final @NotNull CustomOperationDeclaration operationDeclaration;

  public CustomRequestValidationFilter(final @NotNull CustomOperationDeclaration declaration) {
    operationDeclaration = declaration;
  }

  @Override
  public OperationInvocation<CustomOperationRequest, Rsp>
  apply(final OperationInvocation<CustomOperationRequest, Rsp> invocation) {
    return (context, request) -> {
      OpInputDataValidator validator = new OpInputDataValidator();
      Data data = request.data();
      OpInputFieldProjection inputProjection = operationDeclaration.inputProjection();

      if (data == null || inputProjection == null)
        return invocation.invoke(context, request);

      validator.validateData(data, inputProjection.varProjection());
      List<? extends DataValidationError> errors = validator.errors();

      return errors.isEmpty()
             ? invocation.invoke(context, request)
             : CompletableFuture.completedFuture(OperationInvocationResult.failure(FilterUtil.validationError(errors)));
    };

  }
}
