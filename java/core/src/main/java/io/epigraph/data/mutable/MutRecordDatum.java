/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.RecordDatum;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface MutRecordDatum extends MutDatum, RecordDatum {

  @Override
  public @NotNull Map<String, ? extends MutData> fieldsData();

  @Override
  public @Nullable MutData getData(@NotNull Field field);

  public default @NotNull MutData getOrCreateMutData(@NotNull Field field) {
    MutData mutData = getData(field);
    if (mutData == null) setData(field, mutData = field.type.createMutableData());
    return mutData;
  }

  // TODO allow Data (auto-convert to MutData)?
  public @NotNull MutRecordDatum setData(@NotNull Field field, @Nullable MutData data);

  public default @NotNull MutRecordDatum setDatum(@NotNull Field field, @NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateMutData(field).setDatum(tag, datum);
    return this;
  }

  public default @NotNull MutRecordDatum setError(@NotNull Field field, @NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateMutData(field).setError(tag, error);
    return this;
  }

  public class Impl extends MutDatum.Impl implements MutRecordDatum {

    private final @NotNull Map<String, @NotNull MutData> fieldsData = new HashMap<>();

    private @Nullable Map<String, @NotNull ? extends MutData> unmodifiableViewOfFieldsData = null;

    protected Impl(@NotNull RecordType type) {
      super(type);
    }

    @Override
    public @NotNull RecordType type() {
      return (RecordType) super.type();
    }

    @Override
    public @NotNull Map<String, ? extends MutData> fieldsData() {
      if (unmodifiableViewOfFieldsData == null) unmodifiableViewOfFieldsData = Unmodifiable.map(fieldsData);
      return unmodifiableViewOfFieldsData;
    }

    @Override
    public @Nullable MutData getData(@NotNull Field field) {
      // TODO check field is known in the hierarchy
      return fieldsData.get(field.name);
    }

    @Override
    public @NotNull MutRecordDatum setData(@NotNull Field field, @Nullable MutData data) {
      // TODO check field is known and compatible with effective field
      if (data == null) {
        fieldsData.remove(field.name);
      } else {
        // TODO check data is compatible with effective field
        fieldsData.put(field.name, data);
      }
      return this;
    }

  }


}
