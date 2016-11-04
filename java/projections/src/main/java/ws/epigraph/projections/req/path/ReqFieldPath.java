package ws.epigraph.projections.req.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFieldPath extends AbstractFieldProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>
    > {
  @NotNull
  private final ReqParams reqParams;

  public ReqFieldPath(
      @NotNull ReqParams reqParams,
      @NotNull Annotations annotations,
      @NotNull ReqVarPath projection,
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
    ReqFieldPath that = (ReqFieldPath) o;
    return Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams);
  }
}
