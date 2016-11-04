package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqUpdateModelProjection<
    MP extends ReqUpdateModelProjection</*MP*/?, M>,
    M extends DatumType>
    extends AbstractModelProjection<MP, M> {

  protected final boolean update;
  @NotNull
  protected final ReqParams params;

  public ReqUpdateModelProjection(
      @NotNull M model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    super(model, null, annotations, location);
    this.update = update;
    this.params = params;
  }

  /**
   * @return {@code true} if this model must be updated (replaced), {@code false} if it must be patched
   */
  public boolean update() { return update; }

  @NotNull
  public ReqParams params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqUpdateModelProjection<?, ?> that = (ReqUpdateModelProjection<?, ?>) o;
    return update == that.update &&
           Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), update, params);
  }
}
