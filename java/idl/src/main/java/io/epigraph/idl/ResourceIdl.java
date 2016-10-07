package io.epigraph.idl;

import io.epigraph.idl.operations.OperationIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.types.DataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ResourceIdl {
  @NotNull
  private final String fieldName;
  @NotNull
  private final DataType fieldType;
  @NotNull
  private final List<OperationIdl> operations;
  @NotNull
  private final TextLocation location;

  public ResourceIdl(@NotNull String fieldName,
                     @NotNull DataType fieldType,
                     @NotNull List<OperationIdl> operations,
                     @NotNull TextLocation location) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.operations = operations;
    this.location = location;
  }

  @NotNull
  public String fieldName() { return fieldName; }

  @NotNull
  public DataType fieldType() { return fieldType; }

  @NotNull
  public List<OperationIdl> operations() { return operations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public String toString() { return "resource /" + fieldName; }
}
