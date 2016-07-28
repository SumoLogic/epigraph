/* Created by yegor on 7/27/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Value;
import io.epigraph.data.base.ModifiableValueBase;
import io.epigraph.errors.Error;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public class MutValue extends ModifiableValueBase<MutValue> {

  public MutValue(@NotNull DatumType type) { super(type); }

  // TODO take different type as parameter?
  public static @NotNull MutValue from(@NotNull Value value) { // TODO move to parameterized static method in ModifiableValueBase
    if (value instanceof MutValue) return (MutValue) value;
    MutValue mutValue = new MutValue(value.type());
    Error error = value.getError();
    // TODO private constructor with relaxed datum checks?
    return error == null ? mutValue.setDatum(value.getDatum()) : mutValue.setError(error);
  }

}
