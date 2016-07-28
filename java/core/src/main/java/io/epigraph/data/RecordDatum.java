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

  /**
   * @return Unmodifiable mapping of field names to their data. The data could be modifiable.
   */
  public @NotNull Map<String, ? extends Data> fieldsData();

  public @Nullable Data getData(@NotNull Field field);

  public @Nullable Datum getDatum(Field field, Tag tag);

  public @Nullable Error getError(Field field, Tag tag);

  @Override
  public @NotNull ImmRecordDatum toImmutable();

}
