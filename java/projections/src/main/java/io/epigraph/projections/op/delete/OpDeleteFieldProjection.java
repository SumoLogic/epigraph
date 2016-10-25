package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteFieldProjection extends AbstractFieldProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>
    > {

  @NotNull
  private final OpParams params;

  public OpDeleteFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteVarProjection projection,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.params = params;
  }

  @NotNull
  public OpParams params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteFieldProjection that = (OpDeleteFieldProjection) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
