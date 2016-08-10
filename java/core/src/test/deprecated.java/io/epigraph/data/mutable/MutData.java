/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.base.DataBase;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.Type;
import io.epigraph.util.Self;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MutData extends DataBase<MutValue, MutDatum> implements Self<MutData> {

  private final @NotNull Map<String, @NotNull MutValue> values = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends MutValue> unmodifiableViewOfValues = null;

  public MutData(@NotNull Type type) { super(type); }

  @Override
  public @NotNull Map<@NotNull String, @NotNull ? extends MutValue> values() {
    if (unmodifiableViewOfValues == null) unmodifiableViewOfValues = Unmodifiable.map(values);
    return unmodifiableViewOfValues;
  }

  // TODO accept Value and auto-convert? convert on write (NO - someone might hold the reference already)?
  public MutData setValue(Type.Tag tag, @Nullable MutValue value) {
    // TODO check tag compatibility with this.type
    if (value == null) {
      values.remove(tag.name);
    } else {
      // TODO check value compatibility with the tag
      values.put(tag.name, value);
    }
    return self();
  }

  public @NotNull MutData setDatum(
      @NotNull Type.Tag tag,
      @Nullable MutDatum datum
  ) { // TODO take Datum and auto-convert?
    getOrCreateTagValue(tag).setDatum(datum);
    return self();
  }

  public @NotNull MutData setError(@NotNull Type.Tag tag, @NotNull ErrorValue error) {
    getOrCreateTagValue(tag).setError(error);
    return self();
  }

  public @NotNull MutValue getOrCreateTagValue(@NotNull Type.Tag tag) {
    // TODO check tag compatibility with this.type
    MutValue myValue = getValue(tag);
    // TODO this (as many other places) is not thread-safe - use ConcurrentHashMap?
    if (myValue == null) values.put(tag.name, myValue = tag.createMutableValue());
    return myValue;
  }

}
