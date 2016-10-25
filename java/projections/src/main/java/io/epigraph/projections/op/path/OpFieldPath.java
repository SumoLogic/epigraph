package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldPath extends AbstractFieldProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>
    > {

  @NotNull
  private final OpParams params;

  public OpFieldPath(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpVarPath projection,
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
    OpFieldPath that = (OpFieldPath) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
