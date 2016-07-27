/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.AnonListTypeName;
import org.jetbrains.annotations.NotNull;

public class AnonListType extends ListType {

  public AnonListType(boolean polymorphic, @NotNull Type elementType) {
    super(AnonListTypeName.of(polymorphic, elementType.name()), polymorphic, elementType);
  }

  @Override
  public @NotNull AnonListTypeName name() { return (AnonListTypeName) super.name(); }

}
