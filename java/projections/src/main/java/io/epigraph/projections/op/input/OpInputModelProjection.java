package io.epigraph.projections.op.input;

import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpInputModelProjection<M extends DatumType, D extends Datum>
    extends GenericModelProjection<M> {

  protected final boolean required;
  @Nullable
  protected final D defaultValue;
  @Nullable
  protected final OpCustomParams customParams;
  @Nullable
  protected final OpInputModelProjection<?, ?> metaProjection;

  public OpInputModelProjection(@NotNull M model,
                                boolean required,
                                @Nullable D defaultValue,
                                @Nullable OpCustomParams customParams,
                                @Nullable OpInputModelProjection<?, ?> metaProjection,
                                @NotNull TextLocation location) {
    super(model, customParams, location);
    this.required = required;
    this.defaultValue = defaultValue;
    this.customParams = customParams;
    this.metaProjection = metaProjection;
  }

  public boolean required() { return required; }

  @Nullable
  public D defaultValue() { return defaultValue; }

  @Nullable
  public OpCustomParams customParams() { return customParams; }

  @Nullable
  public OpInputModelProjection<?, ?> metaProjection() { return metaProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputModelProjection<?, ?> that = (OpInputModelProjection<?, ?>) o;
    return required == that.required &&
           Objects.equals(defaultValue, that.defaultValue) &&
           Objects.equals(customParams, that.customParams) &&
           Objects.equals(metaProjection, that.metaProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, defaultValue, customParams, metaProjection);
  }
}
