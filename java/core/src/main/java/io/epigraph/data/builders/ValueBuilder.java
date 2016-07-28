/* Created by yegor on 7/27/16. */

package io.epigraph.data.builders;

import io.epigraph.data.base.ModifiableValueBase;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public class ValueBuilder extends ModifiableValueBase<ValueBuilder> {

  public ValueBuilder(@NotNull DatumType type) { super(type); }

}
