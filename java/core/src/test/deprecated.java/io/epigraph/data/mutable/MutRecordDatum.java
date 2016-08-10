/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.RecordDatum;
import io.epigraph.data.base.RecordDatumBase;
import io.epigraph.data.immutable.ImmRecordDatum;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import io.epigraph.util.Self;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MutRecordDatum extends RecordDatumBase<MutData> implements MutDatum, RecordDatum, Self<MutRecordDatum> {

  private final @NotNull Map<String, @NotNull MutData> fieldsData = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends MutData> unmodifiableViewOfFieldsData = null;

  protected MutRecordDatum(@NotNull RecordType type) { super(type); }

  @Override
  public @NotNull Map<String, ? extends MutData> fieldsData() {
    if (unmodifiableViewOfFieldsData == null) unmodifiableViewOfFieldsData = Unmodifiable.map(fieldsData);
    return unmodifiableViewOfFieldsData;
  }

  // TODO allow Data (auto-convert to MutData)?
  public @NotNull MutRecordDatum setData(@NotNull Field field, @Nullable MutData data) {
    // TODO check field is known and compatible with effective field
    type().assertWritable(field);
    if (data == null) {
      fieldsData.remove(field.name);
    } else {
      // TODO check data is compatible with effective field
      fieldsData.put(field.name, data);
    }
    return self();
  }

  public @NotNull MutData getOrCreateFieldData(@NotNull Field field) {
    MutData data = getData(field);
    if (data == null) setData(field, data = field.type.createMutableData());
    return data;
  }

  // TODO take Datum and auto-convert?
  public @NotNull MutRecordDatum setDatum(@NotNull Field field, @NotNull Type.Tag tag, @Nullable MutDatum datum) {
    getOrCreateFieldData(field).setDatum(tag, datum);
    return self();
  }

  public @NotNull MutRecordDatum setError(@NotNull Field field, @NotNull Type.Tag tag, @NotNull ErrorValue error) {
    getOrCreateFieldData(field).setError(tag, error);
    return self();
  }

  @Override
  public @NotNull MutDatum toMutable() { return this; }

  @Override
  public @NotNull ImmRecordDatum toImmutable() {
    return null; // FIXME
  }

}
