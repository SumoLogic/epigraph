/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.immutable.ImmData;
import io.epigraph.data.immutable.ImmData.ImmValue;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MutData implements Data { // TODO MutValues?

  private final @NotNull Type type;

  private final @NotNull Map<String, @NotNull MutValue> values; // TODO we should use MutValue - if we decide to guarantee full (deep) mutability

  private @Nullable Map<String, @NotNull ? extends MutValue> unmodifiableViewOfValues = null;

  public MutData(@NotNull Type type) {
    this.type = type;
    switch (type.immediateTags().size()) { // FIXME premature?
      case 1:
        this.values = new HashMap<>(Unmodifiable.hashMapCapacity(1));
        break;
      default:
        this.values = new HashMap<>();
    }
  }

  @Override
  public @NotNull Type type() {
    return type;
  }

  @Override
  public @NotNull Map<String, ? extends MutValue> values() {
    if (unmodifiableViewOfValues == null) unmodifiableViewOfValues = Unmodifiable.map(values);
    return unmodifiableViewOfValues;
  }

  @Override
  public @Nullable MutValue getValue(Type.Tag tag) { // TODO return MutValue?
    // TODO check tag compatibility with this.type
    return values.get(tag.name);
  }

//  public @Nullable MutValue getMutValue(Type.Tag tag) {
//    return Value.toMutable(getValue(tag));
//  }

  // TODO require MutValue? auto-convert? convert on write?
  public MutData setValue(Type.Tag tag, @Nullable MutValue value) {
    // TODO check tag compatibility with this.type
    if (value == null) {
      values.remove(tag.name);
    } else {
      // TODO check value compatibility with the tag
      values.put(tag.name, value);
    }
    return this;
  }

  public @NotNull MutData setDatum(@NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateMutEntry(tag).setDatum(datum);
    return this;
  }

  public @NotNull MutData setError(@NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateMutEntry(tag).setError(error);
    return this;
  }

  public @NotNull MutValue getOrCreateMutEntry(Type.Tag tag) {
    // TODO check tag compatibility with this.type
    MutValue mutValue = getValue(tag);
    if (mutValue == null) {
      mutValue = tag.createMutable();
      values.put(tag.name, mutValue);
    }
    return mutValue;
  }

  @Override
  public @NotNull ImmData toImmutable() {
    return ImmData.from(this); // TODO move from() code here?
  }


  public static class MutValue implements Value { // TODO MutValue?

    private final @NotNull DatumType type;

    private @Nullable Datum datum;

    private @Nullable Error error;

    public MutValue(@NotNull DatumType type) {
      this.type = type;
      this.datum = null;
      this.error = null;
    }

    @Override
    public @NotNull DatumType type() {
      return type;
    }

    @Override
    public @Nullable Datum getDatum() {
      return datum;
    }

    public @NotNull MutValue setDatum(@Nullable Datum datum) {
      // TODO check datum compatibility vs this.type
      this.datum = datum;
      this.error = null;
      return this;
    }

    @Override
    public @Nullable Error getError() {
      return error;
    }

    public @NotNull MutValue setError(@NotNull Error error) {
      this.datum = null;
      this.error = error;
      return this;
    }

    @Override
    public @NotNull ImmValue toImmutable() {
      return ImmValue.from(this);
    }

//    @Override
//    public @NotNull MutValue toMutable() {
//      return this;
//    }

  }


}
