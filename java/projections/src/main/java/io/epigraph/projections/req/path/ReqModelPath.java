package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqModelPath<
    MP extends ReqModelPath</*MP*/?, M>,
    M extends DatumType>
    extends AbstractModelProjection<MP, M> {

  @NotNull
  protected final ReqParams params;

  public ReqModelPath(
      @NotNull M model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    super(model, null, annotations, location);
    this.params = params;
  }

  @NotNull
  public ReqParams params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqModelPath<?, ?> that = (ReqModelPath<?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
