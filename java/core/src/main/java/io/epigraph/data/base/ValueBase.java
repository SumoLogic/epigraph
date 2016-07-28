/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.Value;
import io.epigraph.data.immutable.ImmValue;
import io.epigraph.data.mutable.MutValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public abstract class ValueBase implements Value {

  private final @NotNull DatumType type;

  public ValueBase(@NotNull DatumType type) { this.type = type; }

  @Override
  public @NotNull DatumType type() { return type; }

  @Override
  public @NotNull ImmValue toImmutable() { return ImmValue.from(this); }

  //@Override // TODO do we need this? (then add to Value interface else remove)
  public @NotNull MutValue toMutable() { return MutValue.from(this); }

}
