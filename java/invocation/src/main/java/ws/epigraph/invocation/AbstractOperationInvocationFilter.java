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

package ws.epigraph.invocation;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOperationInvocationFilter<
    Req extends OperationRequest,
    Rsp extends OperationResponse,
    D extends OperationDeclaration> implements OperationInvocationFilter<Req, Rsp, D> {

  @Override
  public OperationInvocation<Req, Rsp, D> apply(final OperationInvocation<Req, Rsp, D> invocation) {
    return new OperationInvocation<Req, Rsp, D>() {

      @Override
      public CompletableFuture<OperationInvocationResult<Rsp>> invoke(
          final @NotNull Req request, final @NotNull OperationInvocationContext context) {

        return AbstractOperationInvocationFilter.this.invoke(invocation, request, context);
      }

      @Override
      public @NotNull D operationDeclaration() {
        return invocation.operationDeclaration();
      }
    };
  }

  protected abstract CompletableFuture<OperationInvocationResult<Rsp>> invoke(
      @NotNull OperationInvocation<Req, Rsp, D> invocation,
      @NotNull Req request,
      @NotNull OperationInvocationContext context);
}
