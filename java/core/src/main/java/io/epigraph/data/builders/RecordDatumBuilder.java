/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.base.ModifiableRecordDatumBase;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import org.jetbrains.annotations.NotNull;

public class RecordDatumBuilder extends ModifiableRecordDatumBase<RecordDatumBuilder, DataBuilder> {

  public RecordDatumBuilder(@NotNull RecordType type) { super(type); }

  @Override
  protected DataBuilder createFieldData(@NotNull Field field) { return field.type.createDataBuilder(); }

}
