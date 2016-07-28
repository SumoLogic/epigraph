/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.Datum;
import io.epigraph.data.RecordDatum;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import io.epigraph.util.Self;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class ModifiableRecordDatumBase<Me extends RecordDatum, MyData extends ModifiableDataBase>
    extends RecordDatumBase<MyData> implements Self<Me> {

  private final @NotNull Map<String, @NotNull MyData> fieldsData = new HashMap<>();

  private @Nullable Map<String, @NotNull ? extends MyData> unmodifiableViewOfFieldsData = null;

  protected ModifiableRecordDatumBase(@NotNull RecordType type) { super(type); }

  @Override
  public @NotNull Map<String, ? extends MyData> fieldsData() {
    if (unmodifiableViewOfFieldsData == null) unmodifiableViewOfFieldsData = Unmodifiable.map(fieldsData);
    return unmodifiableViewOfFieldsData;
  }

  // TODO allow Data (auto-convert to MyData)?
  public @NotNull Me setData(@NotNull Field field, @Nullable MyData data) {
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

  public @NotNull MyData getOrCreateFieldData(@NotNull Field field) {
    MyData data = getData(field);
    if (data == null) setData(field, data = createFieldData(field));
    return data;
  }

  protected abstract MyData createFieldData(@NotNull Field field);

  public @NotNull Me setDatum(@NotNull Field field, @NotNull Type.Tag tag, @Nullable Datum datum) {
    getOrCreateFieldData(field).setDatum(tag, datum);
    return self();
  }

  public @NotNull Me setError(@NotNull Field field, @NotNull Type.Tag tag, @NotNull Error error) {
    getOrCreateFieldData(field).setError(tag, error);
    return self();
  }

}
