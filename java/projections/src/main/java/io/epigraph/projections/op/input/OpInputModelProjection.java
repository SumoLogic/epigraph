package io.epigraph.projections.op.input;

import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractModelProjection;
import io.epigraph.projections.Annotations;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpInputModelProjection<
    MP extends OpInputModelProjection</*MP*/?, M, D>,
    M extends DatumType,
    D extends Datum>
    extends AbstractModelProjection<MP, M> {

  protected final boolean required;
  @Nullable
  protected final D defaultValue;

  public OpInputModelProjection(
      @NotNull M model,
      boolean required,
      @Nullable D defaultValue,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @NotNull TextLocation location) {
    super(model, metaProjection, annotations, location);
    this.required = required;
    this.defaultValue = defaultValue;
  }

  public boolean required() { return required; }

  @Nullable
  public D defaultValue() { return defaultValue; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputModelProjection<?, ?, ?> that = (OpInputModelProjection<?, ?, ?>) o;
    return required == that.required && Objects.equals(defaultValue, that.defaultValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, defaultValue);
  }
}
