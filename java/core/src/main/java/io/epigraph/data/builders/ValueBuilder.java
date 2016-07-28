/* Created by yegor on 7/27/16. */

package io.epigraph.data.builders;

import io.epigraph.data.shared.ValueBase;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public class ValueBuilder extends ValueBase<ValueBuilder> {

  public ValueBuilder(@NotNull DatumType type) { super(type); }

}
