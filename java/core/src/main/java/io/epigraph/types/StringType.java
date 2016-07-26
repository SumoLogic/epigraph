/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class StringType extends PrimitiveType {

  protected StringType(
      QualifiedTypeName name,
      List<? extends StringType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes, polymorphic);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<StringType> immediateSupertypes() {
    return (List<StringType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<StringType> supertypes() {
    return (Collection<StringType>) super.supertypes();
  }

}
