/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Data;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.base.DatumBase;
import io.epigraph.data.base.RecordDatumBase;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ImmRecordDatum extends ImmDatum, RecordDatum {

  @Override
  public @NotNull Map<String, ? extends ImmData> fieldsData();

  public @Nullable ImmData getData(@NotNull Field field);


  public static class Impl extends RecordDatumBase<ImmData> implements ImmRecordDatum {

    private final @NotNull Map<String, @NotNull ? extends ImmData> fieldsData;

    protected Impl(@NotNull RecordDatum recordDatum) {
      super(recordDatum.type());
      // TODO remove the .Impl part if we trust all implementors of the interface
      fieldsData = recordDatum instanceof ImmRecordDatum.Impl
          ? ((ImmRecordDatum.Impl) recordDatum).fieldsData()
          : Unmodifiable.map(recordDatum.fieldsData().entrySet(), Map.Entry::getKey, me -> me.getValue().toImmutable());
    }

    @Override
    public final @NotNull Map<String, ? extends ImmData> fieldsData() { return fieldsData; }

    @Override
    public @NotNull ImmRecordDatum toImmutable() { return this; }

    public static @NotNull ImmRecordDatum from(@NotNull RecordDatum recordDatum) {
      if (recordDatum instanceof ImmRecordDatum) return (ImmRecordDatum) recordDatum;
      // TODO move/delegate all to/from methods to type that should act as (statically) parameterized factory?
      return new ImmRecordDatum.Impl(recordDatum);
    }

  }


}
