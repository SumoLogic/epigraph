package io.epigraph.service;

import io.epigraph.idl.operations.OperationKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationNotFoundException extends Exception {
  @NotNull
  private final String resourceName;
  @NotNull
  private final OperationKind operationKind;
  @Nullable
  private final String operationName;

  public OperationNotFoundException(@NotNull String resourceName,
                                    @NotNull OperationKind operationKind,
                                    @Nullable String operationName) {

    super(
        String.format(
            "%s operation '%s' in resource '%s' not found",
            operationKind, operationName == null ? "<default>" : operationName, resourceName
        )
    );

    this.resourceName = resourceName;
    this.operationKind = operationKind;
    this.operationName = operationName;
  }

  @NotNull
  public String resourceName() { return resourceName; }

  @NotNull
  public OperationKind operationType() { return operationKind; }

  @Nullable
  public String operationName() { return operationName; }
}
