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
import ws.epigraph.service.operations.Operation;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationInvocationChain<Req extends OperationRequest, Rsp extends OperationResponse, O extends Operation<?, Req, Rsp>> {
  private final @NotNull OperationInvocation<Req, Rsp> seed;
  private final @NotNull Map<O, OperationInvocation<Req, Rsp>> invocationsCache = new IdentityHashMap<>();

  protected OperationInvocationChain(final @NotNull OperationInvocation<Req, Rsp> seed) {this.seed = seed;}

  protected abstract @NotNull Iterable<Function<O, OperationInvocationFilter<Req, Rsp>>> filterFactories();

  public @NotNull OperationInvocation<Req, Rsp> invocation(@NotNull O operation) {
    return invocationsCache.computeIfAbsent(operation, o -> buildChain(
        seed,
        StreamSupport.stream(filterFactories().spliterator(), false).map(f -> f.apply(o)).collect(Collectors.toList())
    ));
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
  public static <Req extends OperationRequest, Rsp extends OperationResponse>
  @NotNull OperationInvocation<Req, Rsp> buildChain(
      @NotNull OperationInvocation<Req, Rsp> seed,
      @NotNull Iterable<OperationInvocationFilter<Req, Rsp>> filters
  ) {
    OperationInvocation<Req, Rsp> result = seed;

    for (final OperationInvocationFilter<Req, Rsp> filter : filters) {
      result = filter.apply(result);
    }

    return result;
  }
}
