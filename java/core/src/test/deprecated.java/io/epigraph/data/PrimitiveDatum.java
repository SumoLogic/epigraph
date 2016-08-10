/* Created by yegor on 7/22/16. */

package io.epigraph.data;

import io.epigraph.data.immutable.ImmPrimitiveDatum;
import io.epigraph.data.mutable.MutPrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

public interface PrimitiveDatum<Native> extends Datum { // TODO parameterize with native type?

  @Override
  public @NotNull PrimitiveType type();

  public @NotNull Native getVal();

  @Override
  public @NotNull ImmPrimitiveDatum toImmutable();

  @Override
  public @NotNull MutPrimitiveDatum toMutable();

}
