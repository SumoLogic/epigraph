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

public interface ImmRecordDatum extends ImmDatum, RecordDatum {

  @Override
  public @NotNull Map<String, ? extends ImmData> fieldsData();


  public static class Impl extends ImmDatum.Impl implements ImmRecordDatum {

    private final @NotNull Map<String, @NotNull ? extends ImmData> fieldsData;

    protected Impl(@NotNull RecordDatum recordDatum) {
      super(recordDatum.type());
      if (recordDatum instanceof ImmRecordDatum.Impl) { // TODO remove the .Impl part if we trust all implementors of the interface
        this.fieldsData = ((ImmRecordDatum.Impl) recordDatum).fieldsData();
      } else {
        Map<String, ? extends Data> fieldsData = recordDatum.fieldsData();
        this.fieldsData = fieldsData.entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                me -> me.getValue().toImmutable(),
                ThrowingMerger,
                Unmodifiable.hashMap(fieldsData.size())
            )
        );
      }
    }

    private static final BinaryOperator<ImmData> ThrowingMerger = (v1, v2) -> { throw new IllegalStateException(); };

    @Override
    public @NotNull RecordType type() { return (RecordType) super.type(); }

    @Override
    public final @NotNull Map<String, ? extends ImmData> fieldsData() { return fieldsData; }

    @Override
    public @NotNull ImmRecordDatum toImmutable() { return this; }

    @Override
    public @Nullable Data getData(@NotNull Field field) { return fieldsData.get(field.name); }

    public static @NotNull ImmRecordDatum from(@NotNull RecordDatum recordDatum) {
      if (recordDatum instanceof ImmRecordDatum) return (ImmRecordDatum) recordDatum;
      // TODO move/delegate all to/from methods to type that should act as (statically) parameterized factory?
      return new ImmRecordDatum.Impl(recordDatum);
    }

  }


}
