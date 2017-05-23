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
import ws.epigraph.invocation.filters.CreateRequestValidationFilter;
import ws.epigraph.invocation.filters.CustomRequestValidationFilter;
import ws.epigraph.invocation.filters.ReadResponsePruningFilter;
import ws.epigraph.invocation.filters.UpdateRequestValidationFilter;
import ws.epigraph.schema.operations.*;
import ws.epigraph.service.operations.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationFilterChains<D extends Data> {
  private final @NotNull OperationInvocationFiltersChain<ReadOperationRequest, ReadOperationResponse<D>, ReadOperationDeclaration>
      readChain;
  private final @NotNull OperationInvocationFiltersChain<CreateOperationRequest, ReadOperationResponse<D>, CreateOperationDeclaration>
      createChain;
  private final @NotNull OperationInvocationFiltersChain<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperationDeclaration>
      updateChain;
  private final @NotNull OperationInvocationFiltersChain<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperationDeclaration>
      deleteChain;
  private final @NotNull OperationInvocationFiltersChain<CustomOperationRequest, ReadOperationResponse<D>, CustomOperationDeclaration>
      customChain;

  public OperationFilterChains(
      @NotNull OperationInvocationFiltersChain<ReadOperationRequest, ReadOperationResponse<D>, ReadOperationDeclaration> readChain,
      @NotNull OperationInvocationFiltersChain<CreateOperationRequest, ReadOperationResponse<D>, CreateOperationDeclaration> createChain,
      @NotNull OperationInvocationFiltersChain<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperationDeclaration> updateChain,
      @NotNull OperationInvocationFiltersChain<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperationDeclaration> deleteChain,
      @NotNull OperationInvocationFiltersChain<CustomOperationRequest, ReadOperationResponse<D>, CustomOperationDeclaration> customChain) {

    this.readChain = readChain;
    this.createChain = createChain;
    this.updateChain = updateChain;
    this.deleteChain = deleteChain;
    this.customChain = customChain;
  }

  public @NotNull OperationInvocation<ReadOperationRequest, ReadOperationResponse<D>, ReadOperationDeclaration>
  filterRead(@NotNull OperationInvocation<ReadOperationRequest, ReadOperationResponse<D>, ReadOperationDeclaration> inv) {
    return readChain.filter(inv);
  }

  public @NotNull OperationInvocation<CreateOperationRequest, ReadOperationResponse<D>, CreateOperationDeclaration>
  filterCreate(@NotNull OperationInvocation<CreateOperationRequest, ReadOperationResponse<D>, CreateOperationDeclaration> inv) {
    return createChain.filter(inv);
  }

  public @NotNull OperationInvocation<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperationDeclaration>
  filterUpdate(@NotNull OperationInvocation<UpdateOperationRequest, ReadOperationResponse<D>, UpdateOperationDeclaration> inv) {
    return updateChain.filter(inv);
  }

  public @NotNull OperationInvocation<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperationDeclaration>
  filterDelete(@NotNull OperationInvocation<DeleteOperationRequest, ReadOperationResponse<D>, DeleteOperationDeclaration> inv) {
    return deleteChain.filter(inv);
  }

  public @NotNull OperationInvocation<CustomOperationRequest, ReadOperationResponse<D>, CustomOperationDeclaration>
  filterCustom(@NotNull OperationInvocation<CustomOperationRequest, ReadOperationResponse<D>, CustomOperationDeclaration> inv) {
    return customChain.filter(inv);
  }

  // todo add filters ensuring that req projections are correct wrt op projections
  public static @NotNull <D extends Data> OperationFilterChains<D> defaultFilterChains() {
    return new OperationFilterChains<>(
        //read
        new DefaultOperationInvocationFiltersChain<>(
            Collections.singleton(new ReadResponsePruningFilter<>())
        ),
        //create
        new DefaultOperationInvocationFiltersChain<>(
            Arrays.asList(
                new CreateRequestValidationFilter<>(),
                new ReadResponsePruningFilter<>()
            )
        ),
        //update
        new DefaultOperationInvocationFiltersChain<>(
            Arrays.asList(
                new UpdateRequestValidationFilter<>(),
                new ReadResponsePruningFilter<>()
            )
        ),
        //delete
        new DefaultOperationInvocationFiltersChain<>(
            Collections.singleton(new ReadResponsePruningFilter<>())
        ),
        //custom
        new DefaultOperationInvocationFiltersChain<>(
            Arrays.asList(
                new CustomRequestValidationFilter<>(),
                new ReadResponsePruningFilter<>()
            )
        )
    );
  }
}
