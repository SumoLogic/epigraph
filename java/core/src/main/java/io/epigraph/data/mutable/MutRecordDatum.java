/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Datum;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.base.ModifiableRecordDatumBase;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MutRecordDatum extends MutDatum, RecordDatum {

  @Override
  public @NotNull Map<String, ? extends MutData> fieldsData();

  @Override
  public @Nullable MutData getData(@NotNull Field field);

  public @NotNull MutData getOrCreateFieldData(@NotNull Field field);

  // TODO allow Data (auto-convert to MutData)?
  public @NotNull MutRecordDatum setData(@NotNull Field field, @Nullable MutData data);

  public @NotNull MutRecordDatum setDatum(@NotNull Field field, @NotNull Type.Tag tag, @Nullable Datum datum);

  public @NotNull MutRecordDatum setError(@NotNull Field field, @NotNull Type.Tag tag, @NotNull Error error);


  public class Impl extends ModifiableRecordDatumBase<MutRecordDatum.Impl, MutData> implements MutRecordDatum {

    protected Impl(@NotNull RecordType type) {
      super(type);
    }

    @Override
    protected MutData createFieldData(@NotNull Field field) { return field.type.createMutableData(); }

  }


}
