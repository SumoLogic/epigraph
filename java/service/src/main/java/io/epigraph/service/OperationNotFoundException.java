package io.epigraph.service;

import io.epigraph.idl.operations.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationNotFoundException extends Exception {
  @NotNull
  private final String resourceName;
  @NotNull
  private final OperationType operationType;
  @Nullable
  private final String operationName;

  public OperationNotFoundException(@NotNull String resourceName,
                                    @NotNull OperationType operationType,
                                    @Nullable String operationName) {

    super(
        String.format(
            "%s operation '%s' in resource '%s' not found",
            operationType, operationName == null ? "<default>" : operationName, resourceName
        )
    );

    this.resourceName = resourceName;
    this.operationType = operationType;
    this.operationName = operationName;
  }

  @NotNull
  public String resourceName() { return resourceName; }

  @NotNull
  public OperationType operationType() { return operationType; }

  @Nullable
  public String operationName() { return operationName; }
}
