/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.StringDatum;
import io.epigraph.data.immutable.ImmStringDatum;
import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringDatumBuilder extends PrimitiveDatumBuilder implements StringDatum {

  private @Nullable String val = null;

  public StringDatumBuilder(@NotNull StringType type) {
    super(type);
  }

  @Override
  public @NotNull StringType type() {
    return (StringType) super.type();
  }

  @Override
  public @NotNull String getVal() throws IllegalStateException {
    if (val == null) throw new IllegalStateException();
    return val;
  }

  public StringDatumBuilder set(@Nullable String val) {
    this.val = val; // TODO validate via type()?
    return this;
  }

  @Override
  public @NotNull ImmStringDatum toImmutable() {
    if (val == null) throw new IllegalStateException();
    return new ImmStringDatum.Impl(type(), val);
  }

}
