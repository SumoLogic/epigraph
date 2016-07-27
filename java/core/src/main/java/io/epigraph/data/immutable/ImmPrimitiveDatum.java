/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

public interface ImmPrimitiveDatum extends ImmDatum, PrimitiveDatum {

  public static abstract class Impl extends ImmDatum.Impl implements ImmPrimitiveDatum {

    protected Impl(PrimitiveType type) {
      super(type);
    }

    @Override
    public @NotNull PrimitiveType type() {
      return (PrimitiveType) super.type();
    }

    @Override
    public abstract @NotNull ImmPrimitiveDatum toImmutable();
  }

}
