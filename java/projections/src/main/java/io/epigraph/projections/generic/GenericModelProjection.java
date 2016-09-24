package io.epigraph.projections.generic;

import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericModelProjection<M extends DatumType> {
  @NotNull
  protected final M model;
  @Nullable
  protected final OpCustomParams customParams;

  public GenericModelProjection(@NotNull M model,
                                @Nullable OpCustomParams customParams) {
    this.model = model;
    this.customParams = customParams;
  }

  public M model() { return model; }

  @Nullable
  public OpCustomParams customParams() { return customParams; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericModelProjection<?> that = (GenericModelProjection<?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(customParams, that.customParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, customParams);
  }
}
