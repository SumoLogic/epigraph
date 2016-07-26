/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.Datum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public abstract class ImmDatum implements Datum {

  private final DatumType type;

  protected ImmDatum(DatumType type) {
    this.type = type;
  }

  @Override
  public @NotNull DatumType type() {
    return type;
  }

}
