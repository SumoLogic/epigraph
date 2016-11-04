/* Created by yegor on 7/22/16. */

package ws.epigraph.names;

import ws.epigraph.data.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnonMapTypeName implements TypeName, Immutable {

  public final @NotNull TypeName keyTypeName;

  public final @NotNull DataTypeName valueTypeName;

  private @Nullable String toString = null;

  public AnonMapTypeName(@NotNull TypeName keyTypeName, @NotNull DataTypeName valueTypeName) {
    this.keyTypeName = keyTypeName;
    this.valueTypeName = valueTypeName;
  }

  @Override
  public @NotNull String toString() {
    if (toString == null) toString = "map[" + keyTypeName + "," + valueTypeName + "]";
    return toString;
  }

}
