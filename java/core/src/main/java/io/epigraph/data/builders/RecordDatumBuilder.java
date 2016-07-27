/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

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

public class RecordDatumBuilder extends DatumBuilder implements RecordDatum {

  private final @NotNull Map<String, @NotNull DataBuilder> fieldsData = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends DataBuilder> unmodifiableViewOfFieldsData = null;

  public RecordDatumBuilder(@NotNull RecordType type) {
    super(type);
  }

  @Override
  public @NotNull RecordType type() {
    return (RecordType) super.type();
  }

  @Override
  public @NotNull Map<String, ? extends DataBuilder> fieldsData() {
    if (unmodifiableViewOfFieldsData == null) unmodifiableViewOfFieldsData = Unmodifiable.map(fieldsData);
    return unmodifiableViewOfFieldsData;
  }

  @Override
  public @Nullable DataBuilder getData(@NotNull Field field) {
    // TODO check field is known in the hierarchy
    return fieldsData.get(field.name);
  }

  // TODO allow Data (auto-convert to DataBuilder)?
  public @NotNull RecordDatumBuilder setData(@NotNull Field field, @Nullable DataBuilder data) {
    // TODO check field is known and compatible with effective field
    if (data == null) {
      fieldsData.remove(field.name);
    } else {
      // TODO check data is compatible with effective field
      fieldsData.put(field.name, data);
    }
    return this;
  }

  public @NotNull DataBuilder getOrCreateDataBuilder(@NotNull Field field) {
    DataBuilder dataBuilder = getData(field);
    if (dataBuilder == null) setData(field, dataBuilder = field.type.createDataBuilder());
    return dataBuilder;
  }

  public @NotNull RecordDatumBuilder setDatum(@NotNull Field field, @NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateDataBuilder(field).setDatum(tag, datum);
    return this;
  }

  public @NotNull RecordDatumBuilder setError(@NotNull Field field, @NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateDataBuilder(field).setError(tag, error);
    return this;
  }

}
