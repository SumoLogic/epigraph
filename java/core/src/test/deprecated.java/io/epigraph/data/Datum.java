/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmDatum;
import io.epigraph.data.mutable.MutDatum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Datum {

  public @NotNull DatumType type();

  public @NotNull ImmDatum toImmutable();

  @Contract("null -> null; !null -> !null")
  public static @Nullable ImmDatum toImmutable(@Nullable Datum datum) {
    return datum == null ? null : datum.toImmutable();
  }

  public @NotNull MutDatum toMutable(); // TODO this should delegate to type().toMutable(ThisDatum)

  @Contract("null -> null; !null -> !null")
  public static @Nullable MutDatum toMutable(@Nullable Datum datum) {
    return datum == null ? null : datum.toMutable();
  }

}
