/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

// TODO parameterize with native type (one of String, Integer, Long, Double, Boolean)?
public abstract class PrimitiveDatumBuilder extends DatumBuilder implements PrimitiveDatum {

  public PrimitiveDatumBuilder(@NotNull PrimitiveType type) {
    super(type);
  }

  @Override
  public @NotNull PrimitiveType type() {
    return (PrimitiveType) super.type();
  }

}
