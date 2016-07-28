/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.Data;
import io.epigraph.data.shared.DataBase;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

public class DataBuilder extends DataBase<DataBuilder, ValueBuilder> implements Data {

  public DataBuilder(@NotNull Type type) { super(type); }

  @Override
  protected ValueBuilder createValue(Type.Tag tag) { return tag.createBuilder(); }


}
