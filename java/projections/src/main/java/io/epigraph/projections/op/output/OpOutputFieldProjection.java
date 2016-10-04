package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection {
  @Nullable
  private final OpParams params;
  @Nullable
  private final Annotations annotations;
  @NotNull
  private final OpOutputVarProjection projection;
  private final boolean includeInDefault;
  @NotNull
  private final TextLocation location;

  public OpOutputFieldProjection(@Nullable OpParams params,
                                 @Nullable Annotations annotations,
                                 @NotNull OpOutputVarProjection projection,
                                 boolean includeInDefault,
                                 @NotNull TextLocation location) {
    this.params = params;
    this.annotations = annotations;
    this.projection = projection;
    this.includeInDefault = includeInDefault;
    this.location = location;
  }

  public @Nullable OpParams params() { return params; }

  @Nullable
  public Annotations annotations() { return annotations; }

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
           Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(params, annotations, projection, includeInDefault); }
}
