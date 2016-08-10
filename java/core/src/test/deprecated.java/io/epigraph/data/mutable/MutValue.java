/* Created by yegor on 7/27/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Datum;
import io.epigraph.data.Value;
import io.epigraph.data.base.ValueBase;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import io.epigraph.util.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutValue extends ValueBase<MutDatum> implements Self<MutValue> {

  private @Nullable Object datumOrError;

  public MutValue(@NotNull DatumType type) {
    super(type);
    this.datumOrError = null;
  }

  @Override
  public @Nullable MutDatum getDatum() {
    Object local = datumOrError;
    return local instanceof ErrorValue ? null : (MutDatum) local;
  }

  public @NotNull MutValue setDatum(@Nullable MutDatum datum) { // TODO take Datum and auto-convert (via protected abstract method)?
    // TODO check datum compatibility vs this.type
    datumOrError = datum;
    return self();
  }

  @Override
  public @Nullable ErrorValue getError() {
    Object local = datumOrError;
    return local instanceof ErrorValue ? (ErrorValue) local : null;
  }

  public @NotNull MutValue setError(@NotNull ErrorValue error) { // TODO synchronized?
    datumOrError = error;
    return self();
  }


  // TODO take different type as parameter?
  public static @NotNull MutValue from(@NotNull Value value) {
    if (value instanceof MutValue) return (MutValue) value; // TODO we probably need something like copy()...
    MutValue mutValue = new MutValue(value.type());
    ErrorValue error = value.getError();
    // TODO private constructor with relaxed datum checks?
    return error == null ? mutValue.setDatum(Datum.toMutable(value.getDatum())) : mutValue.setError(error);
  }

}
