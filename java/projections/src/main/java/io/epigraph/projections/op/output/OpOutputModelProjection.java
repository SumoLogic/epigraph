package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.projections.CustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpOutputModelProjection<M extends DatumType> extends GenericModelProjection<M> {
  protected final boolean includeInDefault;
  @Nullable
  protected final OpParams params;
  @Nullable
  protected final OpOutputModelProjection<?> metaProjection;

  public OpOutputModelProjection(@NotNull M model,
                                 boolean includeInDefault,
                                 @Nullable OpParams params,
                                 @Nullable CustomParams customParams,
                                 @Nullable OpOutputModelProjection<?> metaProjection,
                                 @NotNull TextLocation location) {
    super(model, customParams, location);
    this.includeInDefault = includeInDefault;
    this.params = params;
    this.metaProjection = metaProjection;
  }

  public boolean includeInDefault() { return includeInDefault; }

  @Nullable
  public OpParams params() { return params; }

  @Nullable
  public OpOutputModelProjection<?> metaProjection() { return metaProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputModelProjection<?> that = (OpOutputModelProjection<?>) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(params, that.params) &&
           Objects.equals(metaProjection, that.metaProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), includeInDefault, params, metaProjection);
  }
}
