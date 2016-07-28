/* Created by yegor on 7/27/16. */

package io.epigraph.data.base;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.immutable.ImmData;
import io.epigraph.errors.Error;
import io.epigraph.types.Type;
import io.epigraph.util.Self;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class ModifiableDataBase<Me extends ModifiableDataBase<Me, MyValue>, MyValue extends ModifiableValueBase<MyValue>>
    implements Data, Self<Me> {

  private final @NotNull Type type;

  private final @NotNull Map<String, @NotNull MyValue> values = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends MyValue> unmodifiableViewOfValues = null;

  public ModifiableDataBase(@NotNull Type type) { this.type = type; }

  @Override
  public @NotNull Type type() { return type; }

  @Override
  public @NotNull Map<@NotNull String, @NotNull ? extends MyValue> values() {
    if (unmodifiableViewOfValues == null) unmodifiableViewOfValues = Unmodifiable.map(values);
    return unmodifiableViewOfValues;
  }

  @Override
  public @Nullable MyValue getValue(Type.Tag tag) {
    // TODO check tag compatibility with this.type
    return values.get(tag.name);
  }

  // TODO accept Value and auto-convert? convert on write (NO - someone might hold the reference already)?
  public Me setValue(Type.Tag tag, @Nullable MyValue value) {
    // TODO check tag compatibility with this.type
    if (value == null) {
      values.remove(tag.name);
    } else {
      // TODO check value compatibility with the tag
      values.put(tag.name, value);
    }
    return self();
  }

  public @NotNull Me setDatum(@NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateTagValue(tag).setDatum(datum);
    return self();
  }

  public @NotNull Me setError(@NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateTagValue(tag).setError(error);
    return self();
  }

  public @NotNull MyValue getOrCreateTagValue(Type.Tag tag) {
    // TODO check tag compatibility with this.type
    MyValue myValue = getValue(tag);
    // TODO this (as many other places) is not thread-safe - use ConcurrentHashMap?
    if (myValue == null) values.put(tag.name, myValue = createTagValue(tag));
    return myValue;
  }

  protected abstract MyValue createTagValue(Type.Tag tag);

  @Override
  public @NotNull ImmData toImmutable() { return ImmData.from(this); }

//  @Override
//  public @NotNull MutData toMutable() { return MutData.from(this); }

}
