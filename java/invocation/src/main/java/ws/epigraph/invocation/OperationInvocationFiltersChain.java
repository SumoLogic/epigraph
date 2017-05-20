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

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationInvocationFiltersChain<
    Req extends OperationRequest,
    Rsp extends OperationResponse,
    D extends OperationDeclaration> {

  private final @NotNull Map<OperationInvocation<Req, Rsp, D>, OperationInvocation<Req, Rsp, D>> invocationsCache =
      new IdentityHashMap<>();

  protected abstract @NotNull Iterable<OperationInvocationFilter<Req, Rsp, D>> filters();

  public @NotNull OperationInvocation<Req, Rsp, D> filter(@NotNull OperationInvocation<Req, Rsp, D> inv) {
    return invocationsCache.computeIfAbsent(inv, o -> buildChain(inv, filters()));
  }

  /**
   * Builds a chain of operation invocation filters terminating with given operation invocation
   * implementation to be called first
   *
   * @param seed    initial operation invocation to be called first
   * @param filters a chain of filters to be applied in given order
   * @param <Req>   operation request type
   * @param <Rsp>   operation response type
   *
   * @return {@code OperationInvocation} instance combining all the filters with
   * {@code seed} at the deepest level
   */
  private static <Req extends OperationRequest, Rsp extends OperationResponse, D extends OperationDeclaration>
  @NotNull OperationInvocation<Req, Rsp, D> buildChain(
      @NotNull OperationInvocation<Req, Rsp, D> seed,
      @NotNull Iterable<OperationInvocationFilter<Req, Rsp, D>> filters) {

    OperationInvocation<Req, Rsp, D> result = seed;

    for (final OperationInvocationFilter<Req, Rsp, D> filter : filters) {
      result = filter.apply(result);
    }

    return result;
  }
}
