package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.AbstractOpFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection extends AbstractOpFieldProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>
    > {

  private final boolean includeInDefault;

  public OpOutputFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpOutputVarProjection projection,
      boolean includeInDefault,
      @NotNull TextLocation location) {
    super(params, annotations, projection, location);
    this.includeInDefault = includeInDefault;
  }

  public boolean includeInDefault() { return includeInDefault; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputFieldProjection that = (OpOutputFieldProjection) o;
    return includeInDefault == that.includeInDefault;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), includeInDefault);
  }
}
