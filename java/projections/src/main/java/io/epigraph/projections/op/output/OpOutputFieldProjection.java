package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection extends AbstractFieldProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>
    > {

  @NotNull
  private final OpParams params;
  private final boolean includeInDefault;

  public OpOutputFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpOutputVarProjection projection,
      boolean includeInDefault,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.params = params;
    this.includeInDefault = includeInDefault;
  }

  @NotNull
  public OpParams params() { return params; }

  public boolean includeInDefault() { return includeInDefault; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputFieldProjection that = (OpOutputFieldProjection) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params, includeInDefault);
  }
}
