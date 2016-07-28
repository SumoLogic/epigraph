/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Data;
import io.epigraph.data.Value;
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
  public Value getValue(Type.Tag tag) {
    return null;
  }


}
