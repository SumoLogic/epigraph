/* Created by yegor on 7/27/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmValue;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Value {

  public @NotNull DatumType type(); // TODO do we need this method at all? (yes, in modifiable values - for type checking in setData())

  public @Nullable Datum getDatum();

  public @Nullable ErrorValue getError();

  public @NotNull ImmValue toImmutable();

  @Contract("null -> null; !null -> !null")
  public static @Nullable ImmValue toImmutable(@Nullable Value value) {
    return value == null ? null : value.toImmutable();
  }

//    public @NotNull MutValue toMutable(); // TODO not sure we need this
//
//    @Contract("null -> null; !null -> !null")
//    public static @Nullable MutValue toMutable(@Nullable Value value) {
//      return value == null ? null : value.toMutable();
//    }

}
