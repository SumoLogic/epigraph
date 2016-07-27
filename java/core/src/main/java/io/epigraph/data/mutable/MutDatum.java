/* Created by yegor on 7/26/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.Datum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public interface MutDatum extends Datum {

  public static abstract class Impl implements MutDatum {

    private final @NotNull DatumType type;

    protected Impl(@NotNull DatumType type) {
      this.type = type;
    }

    @Override
    public @NotNull DatumType type() {
      return type;
    }

  }

}