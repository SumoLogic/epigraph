/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Data;
import io.epigraph.data.base.ModifiableDataBase;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

public class MutData extends ModifiableDataBase<MutData, MutValue> implements Data {

  public MutData(@NotNull Type type) { super(type); }

  @Override
  protected MutValue createTagValue(Type.Tag tag) { return tag.createMutable(); }

}
