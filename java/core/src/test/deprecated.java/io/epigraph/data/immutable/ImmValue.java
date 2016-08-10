/* Created by yegor on 7/27/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Datum;
import io.epigraph.data.Value;
import io.epigraph.data.base.ValueBase;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ImmValue extends ValueBase<ImmDatum> {

  private ImmValue(@NotNull DatumType type) { super(type); }

  public static @NotNull ImmValue from(@NotNull Value value) {
    if (value instanceof ImmValue) {
      return (ImmValue) value;
    } else {
      ErrorValue error = value.getError();
      if (error == null) {
        Datum datum = value.getDatum();
        if (datum == null) return new NullImmValue(value.type()); // TODO delegate nulls to the type (and cache there?)
        return new DatumImmValue(value.type(), datum);
      } else {
        return new ErrorImmValue(value.type(), error);
      }
    }
  }

  @Override
  public @NotNull ImmValue toImmutable() { return this; }


  private static final class NullImmValue extends ImmValue {

    public NullImmValue(@NotNull DatumType type) { super(type); }

    @Override
    public @Nullable ImmDatum getDatum() { return null; }

    @Override
    public @Nullable ErrorValue getError() { return null; }

  }


  private static final class DatumImmValue extends ImmValue {

    private final @NotNull ImmDatum datum;

    public DatumImmValue(@NotNull DatumType type, @NotNull Datum datum) {
      // TODO derive type from datum? or do we want to take sub-typed datum?
      super(type);
      this.datum = Datum.toImmutable(datum);
    }

    @Override
    public @NotNull ImmDatum getDatum() { return datum; }

    @Override
    public @Nullable ErrorValue getError() { return null; }

  }


  private static final class ErrorImmValue extends ImmValue {

    private final @NotNull ErrorValue error;

    public ErrorImmValue(@NotNull DatumType type, @NotNull ErrorValue error) {
      super(type);
      this.error = error;
    }

    @Override
    public @Nullable ImmDatum getDatum() { return null; }

    @Override
    public @NotNull ErrorValue getError() { return error; }

  }


}
