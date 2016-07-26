/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmRecordDatum;
import io.epigraph.errors.Error;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type.Tag;
import io.epigraph.types.Type.Tagged;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RecordDatum extends Datum {

  @Override
  @NotNull
  public RecordType type();

  @Override
  @NotNull
  public ImmRecordDatum toImmutable();

  @Nullable
  public Data getData(Field field);

  @Nullable
  public default Datum getDatum(Field field, Tag tag) {
    @Nullable Data data = getData(field);
    return data == null ? null : data.getDatum(tag);
  }

  public default @Nullable Error getError(Field field, Tag tag) {
    @Nullable Data data = getData(field);
    return data == null ? null : data.getError(tag);
  }

  @Nullable
  public default <F extends Field & Tagged> Datum getDatum(F taggedField) { // TODO remove - this is not needed for the "framework" code
    return getDatum(taggedField, taggedField.tag());
  }

  //@Nullable
  //public Datum getDatum2(Field field); // TODO Tag[ged]Field?

}
