/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Datum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public interface ImmDatum extends Datum {

  public static abstract class Impl implements ImmDatum {

    private final DatumType type;

    protected Impl(DatumType type) {
      this.type = type;
    }

    @Override
    public @NotNull DatumType type() {
      return type;
    }

  }

}
