package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqPathModelProjection<
    MP extends ReqPathModelProjection</*MP*/?, M>,
    M extends DatumType>
    extends AbstractModelProjection<MP, M> {

  protected final boolean required;
  @NotNull
  protected final ReqParams params;

  public ReqPathModelProjection(
      @NotNull M model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @NotNull TextLocation location) {
    super(model, metaProjection, annotations, location);
    this.required = required;
    this.params = params;
  }

  public boolean required() { return required; }

  @NotNull
  public ReqParams params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqPathModelProjection<?, ?> that = (ReqPathModelProjection<?, ?>) o;
    return required == that.required &&
           Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, params);
  }
}
