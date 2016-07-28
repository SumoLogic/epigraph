/* Created by yegor on 7/27/16. */

package io.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Self<Self> {

  // This can, of course, be intentionally broken (`class Foo implements Self<Bar>`); good job then.
  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  public default @NotNull Self self() { return (Self) this; }

}
