/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmRecordDatum;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RecordDatum extends Datum {

  @Override
  public @NotNull RecordType type();

  public @NotNull Map<String, ? extends Data> fieldsData();

  @Override
  public default @NotNull ImmRecordDatum toImmutable() { return ImmRecordDatum.Impl.from(this); }

  public @Nullable Data getData(Field field);

  public default @Nullable Datum getDatum(Field field, Tag tag) {
    Data data = getData(field);
    return data == null ? null : data.getDatum(tag);
  }

  public default @Nullable Error getError(Field field, Tag tag) {
    Data data = getData(field);
    return data == null ? null : data.getError(tag);
  }

//  @Nullable
//  public default <F extends Field & Tagged> Datum getDatum(F taggedField) { // TODO remove - this is not needed for "framework" code (so far)
//    return getDatum(taggedField, taggedField.tag());
//  }

//  @Nullable
//  public Datum getDatum2(Field field); // TODO Tag[ged]Field? (NO - unless needed for projections etc.)

}
