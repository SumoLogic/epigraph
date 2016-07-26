/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImmData implements Data {

  private final Type type;

  private final Map<@NotNull String, @NotNull ? extends Value> values;

  private ImmData(Type type, Map<String, Value> values) {
    this.type = type;
    this.values = Unmodifiable.map(values);
  }

  @Override
  public @NotNull ImmData toImmutable() {
    return this;
  }

  public static ImmData from(Data data) {
    if (data instanceof ImmData) {
      return (ImmData) data;
    } else {
      int size = data.values().size();
      final Map<String, Value> entries;
      switch (size) {
        case 1: {
          Map.Entry<String, ? extends Value> entry = data.values().entrySet().iterator().next();
          entries = Collections.singletonMap(entry.getKey(), ImmValue.from(entry.getValue()));
          break;
        }
        case 0: {
          entries = Collections.emptyMap();
          break;
        }
        default: {
          entries = new HashMap<>(Unmodifiable.hashMapCapacity(size));
          for (Map.Entry<String, ? extends Value> entry : data.values().entrySet()) {
            entries.put(entry.getKey(), entry.getValue().toImmutable());
          }
        }
      }
      return new ImmData(data.type(), entries);
    }
  }

  @NotNull
  @Override
  public Type type() {
    return type;
  }

  @NotNull
  @Override
  public Map<String, ? extends Value> values() {
    return values;
  }

  @Override
  @Nullable
  public Data.Value getValue(Type.Tag tag) {
    return null;
  }


  public static class ImmValue implements Value {

    private final @NotNull DatumType type;

    private final @Nullable Datum datum;

    private final @Nullable Error error;

    public ImmValue(@NotNull DatumType type, @Nullable Datum datum) {
      this.type = type;
      this.datum = Datum.toImmutable(datum);
      this.error = null;
    }

    public ImmValue(@NotNull DatumType type, @NotNull Error error) {
      this.type = type;
      this.datum = null;
      this.error = error;
    }

    public static @NotNull ImmValue from(@NotNull Data.Value value) {
      if (value instanceof ImmValue) {
        return (ImmValue) value;
      } else {
        Error error = value.getError();
        return error == null
            ? new ImmValue(value.type(), value.getDatum())
            : new ImmValue(value.type(), error);
      }
    }

    @Override
    public @NotNull DatumType type() {
      return type;
    }

    @Override
    public @Nullable Datum getDatum() {
      return datum;
    }

    @Override
    public @Nullable Error getError() {
      return error;
    }

    @Override
    public @NotNull ImmValue toImmutable() {
      return this;
    }

  }

}
