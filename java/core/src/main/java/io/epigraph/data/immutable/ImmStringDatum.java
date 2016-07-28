/* Created by yegor on 7/25/16. */

package io.epigraph.data.immutable;

import io.epigraph.data.StringDatum;
import io.epigraph.data.base.StringDatumBase;
import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;

public interface ImmStringDatum extends ImmPrimitiveDatum, StringDatum {

  public static abstract class Impl extends StringDatumBase implements ImmStringDatum {

    private final @NotNull String val;

    public Impl(StringType type, @NotNull String val) {
      super(type);
      this.val = val;
    }

    @Override
    public @NotNull String getVal() { return val; }

  }

}
