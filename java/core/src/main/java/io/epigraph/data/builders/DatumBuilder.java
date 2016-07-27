/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.Datum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

public abstract class DatumBuilder implements Datum {

  private final @NotNull DatumType type;

  public DatumBuilder(@NotNull DatumType type) {
    this.type = type;
  }

  @Override
  public @NotNull DatumType type() {
    return type;
  }

}
