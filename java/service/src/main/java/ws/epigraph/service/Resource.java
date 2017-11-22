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

package ws.epigraph.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.HttpMethod;
import ws.epigraph.service.operations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Resource {
  private final @NotNull ResourceDeclaration declaration;

  private final @NotNull Operations<? extends ReadOperation<?>> readOperations;
  private final @NotNull Operations<? extends CreateOperation<?>> createOperations;
  private final @NotNull Operations<? extends UpdateOperation<?>> updateOperations;
  private final @NotNull Operations<? extends DeleteOperation<?>> deleteOperations;

  private final @NotNull Operations<? extends CustomOperation<?>> customGetOperations;
  private final @NotNull Operations<? extends CustomOperation<?>> customPostOperations;
  private final @NotNull Operations<? extends CustomOperation<?>> customPutOperations;
  private final @NotNull Operations<? extends CustomOperation<?>> customDeleteOperations;

  public Resource(
      @NotNull ResourceDeclaration declaration,
      @NotNull Collection<? extends ReadOperation<?>> readOperations,
      @NotNull Collection<? extends CreateOperation<?>> createOperations,
      @NotNull Collection<? extends UpdateOperation<?>> updateOperations,
      @NotNull Collection<? extends DeleteOperation<?>> deleteOperations,
      @NotNull Collection<? extends CustomOperation<?>> customOperations
  ) throws ServiceInitializationException {

    this.declaration = declaration;
    this.readOperations = new Operations<>(declaration.fieldName(), readOperations);
    this.createOperations = new Operations<>(declaration.fieldName(), createOperations);
    this.updateOperations = new Operations<>(declaration.fieldName(), updateOperations);
    this.deleteOperations = new Operations<>(declaration.fieldName(), deleteOperations);

    this.customGetOperations = new Operations<>(
        declaration.fieldName(),
        customOperations.stream()
            .filter(o -> o.declaration().method() == HttpMethod.GET)
            .collect(Collectors.toList())
    );

    this.customPostOperations = new Operations<>(
        declaration.fieldName(),
        customOperations.stream()
            .filter(o -> o.declaration().method() == HttpMethod.POST)
            .collect(Collectors.toList())
    );

    this.customPutOperations = new Operations<>(
        declaration.fieldName(),
        customOperations.stream()
            .filter(o -> o.declaration().method() == HttpMethod.PUT)
            .collect(Collectors.toList())
    );

    this.customDeleteOperations = new Operations<>(
        declaration.fieldName(),
        customOperations.stream()
            .filter(o -> o.declaration().method() == HttpMethod.DELETE)
            .collect(Collectors.toList())
    );

  }

  public @NotNull ResourceDeclaration declaration() { return declaration; }

  /**
   * @return read operations sorted by path length in descending order
   */
  public List<? extends ReadOperation<?>> readOperations() {
    return readOperations.allOperations;
  }

  public @Nullable ReadOperation<?> namedReadOperation(@Nullable String name) {
    return name == null ? readOperations.unnamedOperation : readOperations.namedOperations.get(name);
  }

  /**
   * @return create operations sorted by path length in descending order
   */
  public List<? extends CreateOperation<?>> createOperations() {
    return createOperations.allOperations;
  }

  public @Nullable CreateOperation<?> namedCreateOperation(@Nullable String name) {
    return name == null ? createOperations.unnamedOperation : createOperations.namedOperations.get(name);
  }

  /**
   * @return update operations sorted by path length in descending order
   */
  public List<? extends UpdateOperation<?>> updateOperations() {
    return updateOperations.allOperations;
  }

  public @Nullable UpdateOperation<?> namedUpdateOperation(@Nullable String name) {
    return name == null ? updateOperations.unnamedOperation : updateOperations.namedOperations.get(name);
  }

  /**
   * @return delete operations sorted by path length in descending order
   */
  public List<? extends DeleteOperation<?>> deleteOperations() {
    return deleteOperations.allOperations;
  }

  public @Nullable DeleteOperation<?> namedDeleteOperation(@Nullable String name) {
    return name == null ? deleteOperations.unnamedOperation : deleteOperations.namedOperations.get(name);
  }

  public @Nullable CustomOperation<?> customOperation(@NotNull HttpMethod method, @NotNull String name) {
    final Operations<? extends CustomOperation<?>> ops;
    switch (method) {
      case GET:
        ops = customGetOperations;
        break;
      case POST:
        ops = customPostOperations;
        break;
      case PUT:
        ops = customPutOperations;
        break;
      case DELETE:
        ops = customDeleteOperations;
        break;
      default:
        throw new IllegalArgumentException("Unknown HTTP method: " + method);
    }
    return ops.namedOperations.get(name);
  }

  private static class Operations<O extends Operation<?, ?, ?>> {
    final @Nullable O unnamedOperation;
    final @NotNull Map<String, O> namedOperations;
    final @NotNull List<O> allOperations;

    Operations(@NotNull String resourceName, @NotNull Iterable<O> operations)
        throws ServiceInitializationException {

      namedOperations = new HashMap<>();
      allOperations = new ArrayList<>();

      O _unnamedOperation = null;
      for (O operation : operations) {
        @Nullable String name = operation.declaration().name();
        if (name == null) {
          if (_unnamedOperation == null) {
            _unnamedOperation = operation;
            namedOperations.put(operation.declaration().nameOrDefaultName(), operation); // should still be reachable by name, e.g. ?op=_create
          } else throw new ServiceInitializationException(
              String.format("Default %s operation specified twice for resource '%s'",
                  operation.declaration().kind(), resourceName
              )
          );
        } else {
          if (namedOperations.containsKey(name))
            throw new ServiceInitializationException(
                String.format("%s operation '%s' specified twice for resource '%s'",
                    operation.declaration().kind(), name, resourceName
                )
            );
          else namedOperations.put(name, operation);
        }

        allOperations.add(operation);
      }

      unnamedOperation = _unnamedOperation;

      // sort by path length, from longest to shortest. This is stable sort: operations with the same
      // path length stay in the order of declaration

      allOperations.sort((o1, o2) -> {
        final @Nullable OpFieldProjection path1 = o1.declaration().path();
        final @Nullable OpFieldProjection path2 = o2.declaration().path();

        int path1Len = path1 == null ? 0 : ProjectionUtils.pathLength(path1.projection());
        int path2Len = path2 == null ? 0 : ProjectionUtils.pathLength(path2.projection());

        if (path1Len > path2Len) return -1;
        if (path1Len < path2Len) return 1;
        return 0;
      });
    }
  }

}
