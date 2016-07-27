/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.immutable.ImmData;
import io.epigraph.data.immutable.ImmData.ImmValue;
import io.epigraph.data.mutable.MutData.MutValue;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DataBuilder implements Data {

  private final @NotNull Type type;

  private final @NotNull Map<String, @NotNull ValueBuilder> values = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends ValueBuilder> unmodifiableViewOfValues = null;

  public DataBuilder(@NotNull Type type) { this.type = type; }

  @Override
  public @NotNull Type type() { return type; }

  @Override
  public @NotNull Map<@NotNull String, @NotNull ? extends ValueBuilder> values() {
    if (unmodifiableViewOfValues == null) unmodifiableViewOfValues = Unmodifiable.map(values);
    return unmodifiableViewOfValues;
  }

  @Override
  public @Nullable ValueBuilder getValue(Type.Tag tag) {
    // TODO check tag compatibility with this.type
    return values.get(tag.name);
  }

  // TODO accept Value and auto-convert? convert on write (NO - someone might hold the reference already)?
  public DataBuilder setValue(Type.Tag tag, @Nullable ValueBuilder value) {
    // TODO check tag compatibility with this.type
    if (value == null) {
      values.remove(tag.name);
    } else {
      // TODO check value compatibility with the tag
      values.put(tag.name, value);
    }
    return this;
  }

  public @NotNull DataBuilder setDatum(@NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateMutEntry(tag).setDatum(datum);
    return this;
  }

  public @NotNull DataBuilder setError(@NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateMutEntry(tag).setError(error);
    return this;
  }

  public @NotNull ValueBuilder getOrCreateMutEntry(Type.Tag tag) {
    // TODO check tag compatibility with this.type
    ValueBuilder valueBuilder = getValue(tag);
    // TODO this (as many other places) is not thread-safe - use ConcurrentHashMap?
    if (valueBuilder == null) values.put(tag.name, valueBuilder = tag.createBuilder());
    return valueBuilder;
  }

  @Override
  public @NotNull ImmData toImmutable() { return ImmData.from(this); }


  public static class ValueBuilder implements Value {

    private final @NotNull DatumType type;

    private @Nullable Datum datum;

    private @Nullable Error error;

    public ValueBuilder(@NotNull DatumType type) {
      this.type = type;
      this.datum = null;
      this.error = null;
    }

    @Override
    public @NotNull DatumType type() { return type; }

    @Override
    public @Nullable Datum getDatum() { return datum; }

    public @NotNull ValueBuilder setDatum(@Nullable Datum datum) {
      // TODO check datum compatibility vs this.type
      this.datum = datum;
      this.error = null;
      return this;
    }

    @Override
    public @Nullable Error getError() { return error; }

    public @NotNull ValueBuilder setError(@NotNull Error error) {
      this.datum = null;
      this.error = error;
      return this;
    }

    @Override
    public @NotNull ImmValue toImmutable() { return ImmValue.from(this); }

    //@Override
    public @NotNull MutValue toMutable() { return MutValue.from(this); }

  }


}
