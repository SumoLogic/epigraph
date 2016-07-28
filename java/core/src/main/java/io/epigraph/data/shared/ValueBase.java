/* Created by yegor on 7/27/16. */

package io.epigraph.data.shared;

import io.epigraph.data.Datum;
import io.epigraph.data.Value;
import io.epigraph.data.immutable.ImmValue;
import io.epigraph.data.mutable.MutValue;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.util.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValueBase<Me extends ValueBase<Me>> implements Value, Self<Me> {

  private final @NotNull DatumType type;

  private @Nullable Datum datum;

  private @Nullable Error error;

  public ValueBase(@NotNull DatumType type) {
    this.type = type;
    this.datum = null;
    this.error = null;
  }

  @Override
  public @NotNull DatumType type() { return type; }

  @Override
  public @Nullable Datum getDatum() { return datum; }

  public @NotNull Me setDatum(@Nullable Datum datum) {
    // TODO check datum compatibility vs this.type
    this.datum = datum;
    this.error = null;
    return self();
  }

  @Override
  public @Nullable Error getError() { return error; }

  public @NotNull Me setError(@NotNull Error error) {
    this.datum = null;
    this.error = error;
    return self();
  }

  @Override
  public @NotNull ImmValue toImmutable() { return ImmValue.from(this); }

  //@Override
  public @NotNull MutValue toMutable() { return MutValue.from(this); }

}
