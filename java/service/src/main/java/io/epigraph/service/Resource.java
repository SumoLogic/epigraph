package io.epigraph.service;

import io.epigraph.idl.ResourceIdl;
import io.epigraph.service.operations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Resource {
  @NotNull
  private final ResourceIdl declaration;

  @Nullable
  private final Operations<? extends ReadOperation> readOperations;

  public Resource(
      @NotNull ResourceIdl declaration,
      @Nullable Collection<? extends ReadOperation> readOperations
  ) throws ServiceInitializationException {

    this.declaration = declaration;
    this.readOperations = new Operations<>(declaration.fieldName(), readOperations);
  }

  @NotNull
  public ResourceIdl declaration() { return declaration; }

  @Nullable
  public ReadOperation defaultReadOperation() {
    return readOperations == null ? null : readOperations.defaultOperation;
  }

  @Nullable
  public ReadOperation namedReadOperation(@NotNull String name) {
    return readOperations == null ? null : readOperations.namedOperations.get(name);
  }

  private static class Operations<O extends Operation<?, ?, ?>> {
    @Nullable
    final O defaultOperation;
    @NotNull
    final Map<String, O> namedOperations;

    Operations(@NotNull String resourceName, @Nullable Collection<O> operations)
        throws ServiceInitializationException {
      if (operations == null) {
        defaultOperation = null;
        namedOperations = Collections.emptyMap();
      } else {
        namedOperations = new HashMap<>();
        @Nullable O defaultOperation = null;

        for (O operation : operations) {
          @Nullable String name = operation.declaration().name();
          if (name == null) {
            if (defaultOperation != null)
              throw new ServiceInitializationException(
                  String.format("Default %s operation specified twice for resource '%s'",
                                operation.declaration().type(), resourceName
                  )
              );
            else defaultOperation = operation;
          } else {
            if (namedOperations.containsKey(name))
              throw new ServiceInitializationException(
                  String.format("%s operation '%s' specified twice for resource '%s'",
                                operation.declaration().type(), name, resourceName
                  )
              );
            else namedOperations.put(name, operation);
          }

        }

        this.defaultOperation = defaultOperation;
      }
    }
  }

}
