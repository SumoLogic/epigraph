/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.StringDatum;
import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;

public class ImmStringDatum extends ImmPrimitiveDatum implements StringDatum {

  protected ImmStringDatum(StringType type) {
    super(type);
  }

  @Override
  public @NotNull StringType type() {
    return (StringType) super.type();
  }

  @Override
  public @NotNull ImmStringDatum toImmutable() {
    return this;
  }

}
