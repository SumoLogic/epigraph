package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.CustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection {
  @NotNull
  private final RecordType.Field field;
  @Nullable
  private final OpParams params;
  @Nullable
  private final CustomParams customParams;
  @NotNull
  private final OpOutputVarProjection projection;
  private final boolean includeInDefault;
  @NotNull
  private final TextLocation location;

  public OpOutputFieldProjection(@NotNull RecordType.Field field,
                                 @Nullable OpParams params,
                                 @Nullable CustomParams customParams,
                                 @NotNull OpOutputVarProjection projection,
                                 boolean includeInDefault,
                                 @NotNull TextLocation location) {
    this.field = field;
    this.params = params;
    this.customParams = customParams;
    this.projection = projection;
    this.includeInDefault = includeInDefault;
    this.location = location;
  }

  @NotNull
  public RecordType.Field field() { return field; }

  public @Nullable OpParams params() { return params; }

  @Nullable
  public CustomParams customParams() { return customParams; }

  @NotNull
  public OpOutputVarProjection projection() { return projection; }

  public boolean includeInDefault() { return includeInDefault; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputFieldProjection that = (OpOutputFieldProjection) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(field, that.field) &&
           Objects.equals(params, that.params) &&
           Objects.equals(customParams, that.customParams) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(field, params, customParams, projection, includeInDefault); }
}
