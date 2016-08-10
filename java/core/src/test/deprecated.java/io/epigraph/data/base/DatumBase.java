/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.Datum;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;

// TODO this might need to be overridden by generated static type-specific classes (check parametrization inheritance...)
public abstract class DatumBase<MyDatumType extends DatumType> implements Datum {

  private final MyDatumType type;

  public DatumBase(MyDatumType type) { this.type = type; }

  @Override
  public @NotNull MyDatumType type() { return type; }

}
