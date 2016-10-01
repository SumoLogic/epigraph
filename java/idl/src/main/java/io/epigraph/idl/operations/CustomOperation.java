package io.epigraph.idl.operations;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputVarProjection;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CustomOperation extends Operation {
  private final StepsAndProjection<OpInputVarProjection> inputProjection;

  protected CustomOperation(@Nullable String name,
                            @Nullable OpParams params,
                            @Nullable Annotations annotations,
                            @NotNull OpOutputVarProjection outputProjection,
                            @NotNull StepsAndProjection<OpInputVarProjection> inputProjection,
                            @NotNull TextLocation location) {
    super(OperationType.CUSTOM, name, params, annotations, outputProjection, location);
    this.inputProjection = inputProjection;
  }

  @NotNull
  public StepsAndProjection<OpInputVarProjection> inputProjection() { return inputProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomOperation that = (CustomOperation) o;
    return Objects.equals(inputProjection, that.inputProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inputProjection);
  }
}
