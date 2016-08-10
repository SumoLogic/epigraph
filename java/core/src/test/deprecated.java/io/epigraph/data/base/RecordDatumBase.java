/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.RecordDatum;
import io.epigraph.errors.ErrorValue;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class RecordDatumBase<MyData extends Data> extends DatumBase<RecordType> implements RecordDatum {

  protected RecordDatumBase(@NotNull RecordType type) { super(type); }

  @Contract(pure = true)
  @Override
  public abstract @NotNull Map<String, ? extends MyData> fieldsData();

  @Contract(pure = true)
  @Override
  public @Nullable MyData getData(@NotNull Field field) { return fieldsData().get(type().assertReadable(field).name); }

  @Contract(pure = true)
  @Override
  public @Nullable Datum getDatum(Field field, Type.Tag tag) {
    Data data = getData(field);
    return data == null ? null : data.getDatum(tag);
  }

  @Contract(pure = true)
  @Override
  public @Nullable ErrorValue getError(Field field, Type.Tag tag) {
    Data data = getData(field);
    return data == null ? null : data.getError(tag);
  }

}
