package ws.epigraph.projections.req.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteFieldProjection extends AbstractFieldProjection<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>
    > {
  @NotNull
  private final ReqParams reqParams;

  public ReqDeleteFieldProjection(
      @NotNull ReqParams reqParams,
      @NotNull Annotations annotations,
      @NotNull ReqDeleteVarProjection projection,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.reqParams = reqParams;
  }

  @NotNull
  public ReqParams reqParams() { return reqParams; }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqDeleteFieldProjection that = (ReqDeleteFieldProjection) o;
    return Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams);
  }
}
