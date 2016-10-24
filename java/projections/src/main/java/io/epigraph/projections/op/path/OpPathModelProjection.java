package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractModelProjection;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpPathModelProjection<
    MP extends OpPathModelProjection</*MP*/?, M>,
    M extends DatumType
    > extends AbstractModelProjection<MP, M> {
  @NotNull
  protected final OpParams params;

  public OpPathModelProjection(
      @NotNull M model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @NotNull TextLocation location) {
    super(model, metaProjection, annotations, location);
    this.params = params;
  }

  @NotNull
  public OpParams params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpPathModelProjection<?, ?> that = (OpPathModelProjection<?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
