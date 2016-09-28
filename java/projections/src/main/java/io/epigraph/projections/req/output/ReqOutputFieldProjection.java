package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.CustomParams;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjection {
  @NotNull
  private final RecordType.Field field;
  @Nullable
  private final ReqParams reqParams;
  @Nullable
  private final CustomParams customParams;
  @NotNull
  private final ReqOutputVarProjection projection;
  private final boolean required;
  @NotNull
  private final TextLocation location;

  public ReqOutputFieldProjection(@NotNull RecordType.Field field,
                                  @Nullable ReqParams reqParams,
                                  @Nullable CustomParams customParams,
                                  @NotNull ReqOutputVarProjection projection,
                                  boolean required,
                                  @NotNull TextLocation location) {
    this.field = field;
    this.reqParams = reqParams;
    this.customParams = customParams;
    this.projection = projection;
    this.required = required;
    this.location = location;
  }

  @NotNull
  public RecordType.Field field() { return field; }

  @Nullable
  public ReqParams reqParams() { return reqParams; }

  @Nullable
  public CustomParams customParams() { return customParams; }

  @NotNull
  public ReqOutputVarProjection projection() { return projection; }

  public boolean required() { return required; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqOutputFieldProjection that = (ReqOutputFieldProjection) o;
    return required == that.required &&
           Objects.equals(field, that.field) &&
           Objects.equals(reqParams, that.reqParams) &&
           Objects.equals(customParams, that.customParams) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(field, reqParams, customParams, projection, required); }
}
