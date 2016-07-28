/* Created by yegor on 7/27/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Datum;
import io.epigraph.data.Value;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ImmValue implements Value {

  private final @NotNull DatumType type;

  private ImmValue(@NotNull DatumType type) {
    this.type = type;
  }

  public static @NotNull ImmValue from(@NotNull Value value) {
    if (value instanceof ImmValue) {
      return (ImmValue) value;
    } else {
      Error error = value.getError();
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
  public @NotNull DatumType type() {
    return type;
  }

  @Override
  public @NotNull ImmValue toImmutable() {
    return this;
  }


  private static final class NullImmValue extends ImmValue {

    public NullImmValue(@NotNull DatumType type) { super(type); }

    @Override
    public @Nullable Datum getDatum() { return null; }

    @Override
    public @Nullable Error getError() { return null; }

  }


  private static final class DatumImmValue extends ImmValue {

    private final @NotNull Datum datum;

    public DatumImmValue(@NotNull DatumType type, @NotNull Datum datum) {
      // TODO derive type from datum? or do we allow subtyped datum?
      super(type);
      this.datum = Datum.toImmutable(datum);
    }

    @Override
    public @NotNull Datum getDatum() { return datum; }

    @Override
    public @Nullable Error getError() { return null; }

  }


  private static final class ErrorImmValue extends ImmValue {

    private final @NotNull Error error;

    public ErrorImmValue(@NotNull DatumType type, @NotNull Error error) {
      super(type);
      this.error = error;
    }

    @Override
    public @Nullable Datum getDatum() { return null; }

    @Override
    public @NotNull Error getError() { return error; }

  }


}
