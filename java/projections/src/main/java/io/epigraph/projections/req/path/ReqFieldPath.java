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
public class ReqFieldPath extends AbstractFieldProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>
    > {
  @NotNull
  private final ReqParams reqParams;
  private final boolean required;

  public ReqFieldPath(
      @NotNull ReqParams reqParams,
      @NotNull Annotations annotations,
      @NotNull ReqVarPath projection,
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
    ReqFieldPath that = (ReqFieldPath) o;
    return required == that.required &&
           Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams, required);
  }
}
