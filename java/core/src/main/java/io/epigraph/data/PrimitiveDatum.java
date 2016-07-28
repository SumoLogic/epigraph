/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmPrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

public interface PrimitiveDatum extends Datum {

  @Override
  public @NotNull PrimitiveType type();

  @Override
  public @NotNull ImmPrimitiveDatum toImmutable();

}
