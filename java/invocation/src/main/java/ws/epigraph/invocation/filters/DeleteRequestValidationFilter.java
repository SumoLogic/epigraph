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
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.service.operations.DeleteOperationRequest;
import ws.epigraph.service.operations.OperationResponse;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteRequestValidationFilter<Rsp extends OperationResponse>
    implements OperationInvocationFilter<DeleteOperationRequest, Rsp> {

  private final @NotNull DeleteOperationDeclaration operationDeclaration;

  public DeleteRequestValidationFilter(final @NotNull DeleteOperationDeclaration operationDeclaration) {
    this.operationDeclaration = operationDeclaration;
  }

  @Override
  public OperationInvocation<DeleteOperationRequest, Rsp>
  apply(final OperationInvocation<DeleteOperationRequest, Rsp> invocation) {
    return request -> {
      // todo check that req projection matches op projection and that all leaf
      // req var projections have corresponding op var projections with canDelete = true

      return invocation.invoke(request);
    };
  }
}
