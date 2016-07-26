/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmStringDatum;
import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;

public interface StringDatum extends PrimitiveDatum {

  @NotNull
  @Override
  StringType type();

  @Override
  public @NotNull ImmStringDatum toImmutable();

}
