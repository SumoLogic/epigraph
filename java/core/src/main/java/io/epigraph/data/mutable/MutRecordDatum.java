/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Datum;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.immutable.ImmRecordDatum;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MutRecordDatum extends MutDatum implements RecordDatum {

  private final @NotNull Map<String, @NotNull MutData> fieldsData = new HashMap<>();

  protected MutRecordDatum(@NotNull RecordType type) {
    super(type);
  }

  @Override
  public @NotNull RecordType type() {
    return (RecordType) super.type();
  }

  @Override
  public @Nullable MutData getData(@NotNull Field field) {
    // TODO check field is known in the hierarchy
    return fieldsData.get(field.name);
  }

  public @NotNull MutData getOrCreateMutData(@NotNull Field field) {
    @Nullable MutData data = getData(field);
    if (data == null) {
      data = field.type.createMutableData();
      setData(field, data);
    }
    return data;
  }

  // TODO allow Data and auto-convert to MutData?
  public MutRecordDatum setData(Field field, @Nullable MutData data) {
    // TODO check field is known and compatible with effective field
    if (data == null) {
      fieldsData.remove(field.name);
    } else {
      // TODO check data is compatible with effective field
      fieldsData.put(field.name, data);
    }
    return this;
  }

  public MutRecordDatum setDatum(Field field, Type.Tag tag, @Nullable Datum datum) {
    getOrCreateMutData(field).setDatum(tag, datum);
    return this;
  }

  public MutRecordDatum setError(Field field, Type.Tag tag, @NotNull Error error) {
    getOrCreateMutData(field).setError(tag, error);
    return this;
  }

  @Override
  public @NotNull ImmRecordDatum toImmutable() {
    return null;
  }



}
