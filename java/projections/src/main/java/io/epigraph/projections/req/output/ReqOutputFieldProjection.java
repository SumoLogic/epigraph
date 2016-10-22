package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjection extends AbstractFieldProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>
    > {
  @Nullable
  private final ReqParams reqParams;
  private final boolean required;

  public ReqOutputFieldProjection(
      @Nullable ReqParams reqParams,
      @Nullable Annotations annotations,
      @NotNull ReqOutputVarProjection projection,
      boolean required,
      @NotNull TextLocation location) {
    super(annotations, projection, location);
    this.reqParams = reqParams;
    this.required = required;
  }

  @Nullable
  public ReqParams reqParams() { return reqParams; }

  public boolean required() { return required; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputFieldProjection that = (ReqOutputFieldProjection) o;
    return required == that.required &&
           Objects.equals(reqParams, that.reqParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), reqParams, required);
  }
}
