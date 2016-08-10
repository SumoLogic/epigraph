/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Data;
import io.epigraph.data.Value;
import io.epigraph.data.base.DataBase;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ImmData extends DataBase<ImmValue, ImmDatum> {

  private final Map<@NotNull String, @NotNull ? extends ImmValue> values;

  private ImmData(Type type, Map<String, ? extends ImmValue> values) {
    super(type);
    this.values = Unmodifiable.map(values);
  }

  @Override
  public @NotNull Map<String, ? extends ImmValue> values() { return values; }

  @Override
  public @NotNull ImmData toImmutable() { return this; }

  public static ImmData from(Data data) {
    if (data instanceof ImmData) {
      return (ImmData) data;
    } else {
      int size = data.values().size();
      final Map<String, ImmValue> immValues;
      switch (size) {
        case 1: {
          Map.Entry<String, ? extends Value> entry = data.values().entrySet().iterator().next();
          immValues = Collections.singletonMap(entry.getKey(), ImmValue.from(entry.getValue()));
          break;
        }
        case 0: {
          immValues = Collections.emptyMap();
          break;
        }
        default: {
          immValues = new HashMap<>(Unmodifiable.hashMapCapacity(size));
          for (Map.Entry<String, ? extends Value> entry : data.values().entrySet()) {
            immValues.put(entry.getKey(), entry.getValue().toImmutable());
          }
        }
      }
      return new ImmData(data.type(), immValues);
    }
  }

}
