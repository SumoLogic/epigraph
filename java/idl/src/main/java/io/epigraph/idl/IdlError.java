package io.epigraph.idl;

import io.epigraph.idl.operations.OperationIdl;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlError {
  @Nullable
  private final ResourceIdl resource;
  @Nullable
  private final OperationIdl operation;
  @NotNull
  private final String message;
  @NotNull
  private final TextLocation location;

  public IdlError(
      @Nullable ResourceIdl resource,
      @Nullable OperationIdl operation,
      @NotNull String message,
      @NotNull TextLocation location) {

    this.resource = resource;
    this.operation = operation;
    this.message = message;
    this.location = location;
  }

  @Nullable
  public ResourceIdl resource() { return resource; }

  @Nullable
  public OperationIdl operation() { return operation; }

  @NotNull
  public String message() { return message; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdlError idlError = (IdlError) o;
    return Objects.equals(resource, idlError.resource) &&
           Objects.equals(operation, idlError.operation) &&
           Objects.equals(message, idlError.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resource, operation, message);
  }
}
