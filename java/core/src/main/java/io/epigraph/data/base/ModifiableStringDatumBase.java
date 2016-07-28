/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.StringDatum;
import io.epigraph.types.StringType;
import io.epigraph.util.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModifiableStringDatumBase<Me extends StringDatum> extends StringDatumBase implements Self<Me> {

  private @Nullable String val = null;

  public ModifiableStringDatumBase(StringType type) { super(type); }

  @Override
  public @NotNull String getVal() throws IllegalStateException {
    if (val == null) throw new IllegalStateException();
    return val;
  }

  public @NotNull Me set(@Nullable String val) {
    this.val = val; // TODO validate via type()?
    return self();
  }

}
