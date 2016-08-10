/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.Datum;
import io.epigraph.data.Value;
import io.epigraph.data.immutable.ImmValue;
import io.epigraph.data.mutable.MutValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ValueBase<MyDatum extends Datum> implements Value { // TODO move to Value (make it a class)?

  private final @NotNull DatumType type;

  public ValueBase(@NotNull DatumType type) { this.type = type; }

  @Override
  public @NotNull DatumType type() { return type; }

  @Override
  public abstract @Nullable MyDatum getDatum();

  @Override
  public @NotNull ImmValue toImmutable() { return ImmValue.from(this); }

  //@Override // TODO do we need this? (then add to Value interface else remove)
  public @NotNull MutValue toMutable() { return MutValue.from(this); }

}
