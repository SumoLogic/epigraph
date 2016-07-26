/* Created by yegor on 7/25/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmData;
import io.epigraph.data.immutable.ImmData.ImmValue;
import io.epigraph.data.mutable.MutData.MutValue;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import io.epigraph.types.Type.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Collection of tagged values representing the same "entity".
 */
public interface Data { // TODO Var? Union? Values?

  public @NotNull Type type();

  public @NotNull Map<@NotNull String, @NotNull ? extends Value> values();

  public default @Nullable Datum getDatum(@NotNull Tag tag) {
    Value value = getValue(tag);
    return value == null ? null : value.getDatum();
  }

  public default @Nullable Error getError(Tag tag) {
    Value value = getValue(tag);
    return value == null ? null : value.getError();
  }

  public @Nullable Value getValue(Tag tag);

  public @NotNull ImmData toImmutable();


  public static interface Value {

    public @NotNull DatumType type();

    public @Nullable Datum getDatum();

    public @Nullable Error getError();

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

}
