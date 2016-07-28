/* Created by yegor on 7/27/16. */

package io.epigraph.data.base;

import io.epigraph.data.Datum;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.util.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModifiableValueBase<Me extends ModifiableValueBase<Me>> extends ValueBase implements Self<Me> {

  private @Nullable Datum datum;

  private @Nullable Error error;

  public ModifiableValueBase(@NotNull DatumType type) {
    super(type);
    this.datum = null;
    this.error = null;
  }

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

}
