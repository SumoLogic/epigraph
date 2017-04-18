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
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FailureOperationInvocation<Req extends OperationRequest, Rsp extends OperationResponse>
    implements OperationInvocation<Req, Rsp> {

  private final @NotNull OperationInvocationError error;

  public FailureOperationInvocation(final @NotNull OperationInvocationError error) {this.error = error;}

  @Override
  public @NotNull CompletableFuture<OperationInvocationResult<Rsp>> invoke(final @NotNull Req request) {
    return CompletableFuture.completedFuture(OperationInvocationResult.failure(error));
  }
}
