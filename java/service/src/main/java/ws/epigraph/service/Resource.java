/*
 * Copyright 2016 Sumo Logic
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

import ws.epigraph.idl.ResourceDeclaration;
import ws.epigraph.idl.operations.HttpMethod;
import ws.epigraph.idl.operations.OperationDeclaration;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.service.operations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    verifyCustomOpNameClashes(declaration, customOperations);
  }

  private void verifyCustomOpNameClashes(
      final @NotNull ResourceDeclaration declaration,
      final @NotNull Iterable<? extends CustomOperation<?>> customOperations) throws ServiceInitializationException {

    // check that custom operations don't intersect with the others
    for (final CustomOperation<?> customOperation : customOperations) {
      final OperationDeclaration customDecl = customOperation.declaration();
      final @Nullable String customOpName = customDecl.name();
      if (customOpName == null)
        throw new ServiceInitializationException(
            String.format(
                "Resource '%s': custom operation without a name declared at: %s",
                declaration.fieldName(),
                customDecl.location()
            )
        );

      final Operations<?> ops;
      switch (customDecl.method()) {
        case GET:
          ops = this.readOperations;
          break;
        case POST:
          ops = this.createOperations;
          break;
        case PUT:
          ops = this.updateOperations;
          break;
        case DELETE:
          ops = this.deleteOperations;
          break;
        default:
          throw new ServiceInitializationException("Unknown HTTP method: " + customDecl.method());
      }

      final Operation<?, ?, ?> otherOp = ops.namedOperations.get(customOpName);
      if (otherOp != null)
        throw new ServiceInitializationException(
            String.format(
                "Custom operation '%s declared at: %s clashes with %s operation declared at %s",
                customOpName,
                customOperation.declaration().location(),
                otherOp.declaration().kind(),
                otherOp.declaration().location()
            )
        );
    }
  }

  public @NotNull ResourceDeclaration declaration() { return declaration; }

  /**
   * @return unnamed read operations sorted by path length in descending order
   */
  public List<? extends ReadOperation<?>> unnamedReadOperations() {
    return readOperations.unnamedOperations;
  }

  public @Nullable ReadOperation<?> namedReadOperation(@NotNull String name) {
    return readOperations.namedOperations.get(name);
  }

  /**
   * @return unnamed create operations sorted by path length in descending order
   */
  public List<? extends CreateOperation<?>> unnamedCreateOperations() {
    return createOperations.unnamedOperations;
  }

  public @Nullable CreateOperation<?> namedCreateOperation(@NotNull String name) {
    return createOperations.namedOperations.get(name);
  }

  /**
   * @return unnamed update operations sorted by path length in descending order
   */
  public List<? extends UpdateOperation<?>> unnamedUpdateOperations() {
    return updateOperations.unnamedOperations;
  }

  public @Nullable UpdateOperation<?> namedUpdateOperation(@NotNull String name) {
    return updateOperations.namedOperations.get(name);
  }

  /**
   * @return unnamed delete operations sorted by path length in descending order
   */
  public List<? extends DeleteOperation<?>> unnamedDeleteOperations() {
    return deleteOperations.unnamedOperations;
  }

  public @Nullable DeleteOperation<?> namedDeleteOperation(@NotNull String name) {
    return deleteOperations.namedOperations.get(name);
  }

  public @Nullable CustomOperation<?> customOperation(@NotNull HttpMethod method, @NotNull String name) {
    final Operations<? extends CustomOperation<?>> ops;
    switch (method) {
      case GET:
        ops = this.customGetOperations;
        break;
      case POST:
        ops = this.customPostOperations;
        break;
      case PUT:
        ops = this.customPutOperations;
        break;
      case DELETE:
        ops = this.customDeleteOperations;
        break;
      default:
        throw new IllegalArgumentException("Unknown HTTP method: " + method);
    }
    return ops.namedOperations.get(name);
  }

  private static class Operations<O extends Operation<?, ?, ?>> {
    final @NotNull List<O> unnamedOperations;
    final @NotNull Map<String, O> namedOperations;

    Operations(@NotNull String resourceName, @NotNull Iterable<O> operations)
        throws ServiceInitializationException {

      unnamedOperations = new ArrayList<>();
      namedOperations = new HashMap<>();

      for (O operation : operations) {
        @Nullable String name = operation.declaration().name();
        if (name == null) {
          unnamedOperations.add(operation);
        } else {
          if (namedOperations.containsKey(name))
            throw new ServiceInitializationException(
                String.format("%s operation '%s' specified twice for resource '%s'",
                    operation.declaration().kind(), name, resourceName
                )
            );
          else namedOperations.put(name, operation);
        }
      }

      // sort by path length, from longest to shortest. This is stable sort: operations with the same
      // path length stay in the order of declaration

      unnamedOperations.sort((o1, o2) -> {
        final @Nullable OpFieldPath path1 = o1.declaration().path();
        final @Nullable OpFieldPath path2 = o2.declaration().path();

        int path1Len = path1 == null ? 0 : ProjectionUtils.pathLength(path1.varProjection());
        int path2Len = path2 == null ? 0 : ProjectionUtils.pathLength(path2.varProjection());

        if (path1Len > path2Len) return -1;
        if (path1Len < path2Len) return 1;
        return 0;
      });
    }
  }

}
