package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathFieldProjection extends AbstractFieldProjection<
    ReqPathVarProjection,
    ReqPathTagProjectionEntry,
    ReqPathModelProjection<?, ?>
    > {
  @NotNull
  private final ReqParams reqParams;
  private final boolean required;

  public ReqPathFieldProjection(
      @NotNull ReqParams reqParams,
      @NotNull Annotations annotations,
      @NotNull ReqPathVarProjection projection,
      boolean required,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.reqParams = reqParams;
    this.required = required;
  }

  @NotNull
  public ReqParams reqParams() { return reqParams; }

  public boolean required() { return required; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqPathFieldProjection that = (ReqPathFieldProjection) o;
    return required == that.required &&
           Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams, required);
  }
}
