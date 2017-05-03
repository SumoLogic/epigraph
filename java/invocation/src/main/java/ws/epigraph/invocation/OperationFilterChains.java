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
import ws.epigraph.data.Data;
import ws.epigraph.invocation.filters.*;
import ws.epigraph.service.operations.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationFilterChains<D extends Data> {
  private final @NotNull OperationInvocationFiltersChain<ReadOperationRequest, ReadOperationResponse<D>, ReadOperation<D>>
      readOperationOperationInvocationFiltersChain;
  private final @NotNull OperationInvocationFiltersChain<CreateOperationRequest, ReadOperationResponse<D>, CreateOperation<D>>
      createOperationOperationInvocationFiltersChain;
  private final @NotNull OperationInvocationFiltersChain<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperation<D>>
      updateOperationOperationInvocationFiltersChain;
  private final @NotNull OperationInvocationFiltersChain<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperation<D>>
      deleteOperationOperationInvocationFiltersChain;
  private final @NotNull OperationInvocationFiltersChain<CustomOperationRequest, ReadOperationResponse<D>, CustomOperation<D>>
      customOperationOperationInvocationFiltersChain;

  public OperationFilterChains(
      final @NotNull OperationInvocationFiltersChain<ReadOperationRequest, ReadOperationResponse<D>, ReadOperation<D>> readOperationOperationInvocationFiltersChain,
      final @NotNull OperationInvocationFiltersChain<CreateOperationRequest, ReadOperationResponse<D>, CreateOperation<D>> createOperationOperationInvocationFiltersChain,
      final @NotNull OperationInvocationFiltersChain<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperation<D>> updateOperationOperationInvocationFiltersChain,
      final @NotNull OperationInvocationFiltersChain<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperation<D>> deleteOperationOperationInvocationFiltersChain,
      final @NotNull OperationInvocationFiltersChain<CustomOperationRequest, ReadOperationResponse<D>, CustomOperation<D>> customOperationOperationInvocationFiltersChain) {
    this.readOperationOperationInvocationFiltersChain = readOperationOperationInvocationFiltersChain;
    this.createOperationOperationInvocationFiltersChain = createOperationOperationInvocationFiltersChain;
    this.updateOperationOperationInvocationFiltersChain = updateOperationOperationInvocationFiltersChain;
    this.deleteOperationOperationInvocationFiltersChain = deleteOperationOperationInvocationFiltersChain;
    this.customOperationOperationInvocationFiltersChain = customOperationOperationInvocationFiltersChain;
  }

  public @NotNull OperationInvocation<ReadOperationRequest, ReadOperationResponse<D>>
  readOperationInvocation(@NotNull ReadOperation<D> operation) {
    return readOperationOperationInvocationFiltersChain.invocation(operation);
  }

  public @NotNull OperationInvocation<CreateOperationRequest, ReadOperationResponse<D>>
  createOperationInvocation(@NotNull CreateOperation<D> operation) {
    return createOperationOperationInvocationFiltersChain.invocation(operation);
  }

  public @NotNull OperationInvocation<UpdateOperationRequest, ReadOperationResponse<D>>
  updateOperationInvocation(@NotNull UpdateOperation<D> operation) {
    return updateOperationOperationInvocationFiltersChain.invocation(operation);
  }

  public @NotNull OperationInvocation<DeleteOperationRequest, ReadOperationResponse<D>>
  deleteOperationInvocation(@NotNull DeleteOperation<D> operation) {
    return deleteOperationOperationInvocationFiltersChain.invocation(operation);
  }

  public @NotNull OperationInvocation<CustomOperationRequest, ReadOperationResponse<D>>
  customOperationInvocation(@NotNull CustomOperation<D> operation) {
    return customOperationOperationInvocationFiltersChain.invocation(operation);
  }

  // todo add filters ensuring that req projections are correct wrt op projections
  public static @NotNull <D extends Data> OperationFilterChains<D> defaultLocalFilterChains() {
    return new OperationFilterChains<>(
        //read
        new DefaultOperationInvocationFiltersChain<>(
            LocalOperationInvocation::new,
            Collections.singletonList(operation -> new ReadResponsePruningFilter<>())
        ),
        //create
        new DefaultOperationInvocationFiltersChain<>(
            LocalOperationInvocation::new,
            Arrays.asList(
                operation -> new CreateRequestValidationFilter<>(operation.declaration()),
                operation -> new ReadResponsePruningFilter<>()
            )
        ),
        //update
        new DefaultOperationInvocationFiltersChain<>(
            LocalOperationInvocation::new,
            Arrays.asList(
                operation -> new UpdateRequestValidationFilter<>(operation.declaration()),
                operation -> new ReadResponsePruningFilter<>()
            )
        ),
        //delete
        new DefaultOperationInvocationFiltersChain<>(
            LocalOperationInvocation::new,
            Collections.singletonList(operation -> new ReadResponsePruningFilter<>())
        ),
        //custom
        new DefaultOperationInvocationFiltersChain<>(
            LocalOperationInvocation::new,
            Arrays.asList(
                operation -> new CustomRequestValidationFilter<>(operation.declaration()),
                operation -> new ReadResponsePruningFilter<>()
            )
        )
    );
  }
}
