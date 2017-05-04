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
 * Operation invocation
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface OperationInvocation<Req extends OperationRequest, Rsp extends OperationResponse> {
  /**
   * Invokes an operation returning a future of invocation result
   *
   * @param context operation invocation context
   * @param request request
   *
   * @return future of invocation result
   */
  @NotNull CompletableFuture<OperationInvocationResult<Rsp>> invoke(
      @NotNull OperationInvocationContext context,
      @NotNull Req request
  );
}
