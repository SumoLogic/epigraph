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
public class UpdateOperation extends Operation {
  private final StepsAndProjection<OpInputVarProjection> updateProjection;

  protected UpdateOperation(@Nullable String name,
                            @Nullable OpParams params,
                            @Nullable Annotations annotations,
                            @NotNull OpOutputVarProjection outputProjection,
                            @NotNull StepsAndProjection<OpInputVarProjection> updateProjection,
                            @NotNull TextLocation location) {
    super(OperationType.UPDATE, name, params, annotations, outputProjection, location);
    this.updateProjection = updateProjection;
  }

  @NotNull
  public StepsAndProjection<OpInputVarProjection> updateProjection() { return updateProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UpdateOperation that = (UpdateOperation) o;
    return Objects.equals(updateProjection, that.updateProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updateProjection);
  }
}
