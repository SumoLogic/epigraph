/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class StringType extends PrimitiveType {

  protected StringType(
      @NotNull QualifiedTypeName name,
      @NotNull List<? extends StringType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes, polymorphic);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<? extends StringType> immediateSupertypes() {
    return (List<? extends StringType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<? extends StringType> supertypes() {
    return (Collection<? extends StringType>) super.supertypes();
  }

}
