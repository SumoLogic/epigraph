/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;

public class AnonMapTypeName implements TypeName {

  public final @NotNull TypeName keyTypeName;

  public final @NotNull DataTypeName valueTypeName;

  public AnonMapTypeName(@NotNull TypeName keyTypeName, @NotNull DataTypeName valueTypeName) {
    this.keyTypeName = keyTypeName;
    this.valueTypeName = valueTypeName;
  }

  @Override
  public String toString() { // TODO cache?
    return "map[" + keyTypeName + "," + valueTypeName + "]";
  }

}
