package io.epigraph.idl.operations;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationIdl {
  @NotNull
  private final OperationType type;
  @Nullable
  private final String name;
  @Nullable
  private final OpParams params;
  @Nullable
  private final Annotations annotations;
  @NotNull
  private final OpOutputVarProjection outputProjection;
  @NotNull
  private final TextLocation location;

  protected OperationIdl(@NotNull OperationType type,
                         @Nullable String name,
                         @Nullable OpParams params,
                         @Nullable Annotations annotations,
                         @NotNull OpOutputVarProjection outputProjection,
                         @NotNull TextLocation location) {
    this.type = type;
    this.name = name;
    this.params = params;
    this.annotations = annotations;
    this.outputProjection = outputProjection;
    this.location = location;
  }

  @NotNull
  public OperationType type() { return type; }

  @Nullable
  public String name() { return name; }

  public boolean isDefault() { return name == null; }

  @Nullable
  public OpParams params() { return params; }

  @Nullable
  public Annotations annotations() { return annotations; }

  @NotNull
  public OpOutputVarProjection outputProjection() { return outputProjection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (isDefault()) sb.append("default ");
    else sb.append(name).append(": ");

    sb.append(type);

    return sb.toString();
  }
}
