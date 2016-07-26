/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Data;
import io.epigraph.data.RecordDatum;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class ImmRecordDatum extends ImmDatum implements RecordDatum {

  private final @NotNull Map<String, @NotNull ImmData> fieldsData;

  public ImmRecordDatum(RecordType type, Map<String, Data> fieldsData) {
    super(type);
    this.fieldsData = fieldsData.entrySet().stream().collect(
        Collectors.toMap(
            Map.Entry::getKey,
            e -> e.getValue().toImmutable(),
            ThrowingMerger,
            Unmodifiable.hashMap(fieldsData.size())
        )
    );

  }

  private static final BinaryOperator<ImmData> ThrowingMerger = (v1, v2) -> {
    throw new IllegalStateException();
  };

  @Override
  @NotNull
  public RecordType type() {
    return (RecordType) super.type();
  }

  @Override
  public @NotNull ImmRecordDatum toImmutable() {
    return this;
  }

  @Override
  public @Nullable Data getData(Field field) {
    return fieldsData.get(field.name);
  }

}
