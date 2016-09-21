/* Created by yegor on 7/22/16. */

package io.epigraph.names;

import org.jetbrains.annotations.NotNull;

public class AnonListTypeName implements TypeName {

  public final @NotNull DataTypeName elementTypeName;

  public AnonListTypeName(@NotNull DataTypeName elementTypeName) { this.elementTypeName = elementTypeName; }

  @Override
  public String toString() { // TODO cache?
    return "list[" + elementTypeName + "]";
  }

}
