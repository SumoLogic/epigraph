/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.PrimitiveDatum;
import org.jetbrains.annotations.NotNull;

public interface ImmPrimitiveDatum extends ImmDatum, PrimitiveDatum {

  @Override
  public @NotNull ImmPrimitiveDatum toImmutable();

}
