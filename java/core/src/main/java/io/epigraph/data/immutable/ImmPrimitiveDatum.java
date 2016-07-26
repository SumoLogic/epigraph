/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

public abstract class ImmPrimitiveDatum extends ImmDatum implements PrimitiveDatum {

  protected ImmPrimitiveDatum(PrimitiveType type) {
    super(type);
  }

  @Override
  public @NotNull PrimitiveType type() {
    return (PrimitiveType) super.type();
  }

  @Override
  public abstract @NotNull ImmPrimitiveDatum toImmutable();
}
