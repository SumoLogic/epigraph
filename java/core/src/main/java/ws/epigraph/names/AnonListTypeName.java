/* Created by yegor on 7/22/16. */

package ws.epigraph.names;

import ws.epigraph.data.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AnonListTypeName implements TypeName, Immutable {

  public final @NotNull DataTypeName elementTypeName;

  private @Nullable String toString = null;

  public AnonListTypeName(@NotNull DataTypeName elementTypeName) { this.elementTypeName = elementTypeName; }

  @Override
  public @NotNull String toString() {
    if (toString == null) toString = "list[" + elementTypeName + "]";
    return toString;
  }

}
