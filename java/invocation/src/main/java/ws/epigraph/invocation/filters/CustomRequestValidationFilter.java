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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.data.validation.OpInputDataValidator;
import ws.epigraph.invocation.AbstractOperationInvocationFilter;
import ws.epigraph.invocation.InvocationResult;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.schema.operations.CustomOperationDeclaration;
import ws.epigraph.service.operations.CustomOperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Checks that all parts marked as `required` by operation input projection are actually present
 * in request data
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CustomRequestValidationFilter<Rsp extends OperationResponse>
    extends AbstractOperationInvocationFilter<CustomOperationRequest, Rsp, CustomOperationDeclaration> {

  @Override
  protected CompletableFuture<InvocationResult<Rsp>> invoke(
      final @NotNull OperationInvocation<CustomOperationRequest, Rsp, CustomOperationDeclaration> invocation,
      final @NotNull CustomOperationRequest request,
      final @NotNull OperationInvocationContext context) {

    OpInputDataValidator validator = new OpInputDataValidator();
    Data data = request.data();
    // nullable here is legit but breaks JaCoCo: http://forge.ow2.org/tracker/?func=detail&aid=317789&group_id=23&atid=100023
    /*@Nullable*/ OpFieldProjection inputProjection = invocation.operationDeclaration().inputProjection();

    if (data == null || inputProjection == null)
      return invocation.invoke(request, context);

    validator.validateData(data, inputProjection.varProjection());
    List<? extends DataValidationError> errors = validator.errors();

    return errors.isEmpty()
           ? invocation.invoke(request, context)
           : CompletableFuture.completedFuture(InvocationResult.failure(FilterUtil.validationError(errors)));
  }
}
