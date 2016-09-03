/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class PrimitiveType extends DatumType { // TODO parameterize with native type?

  protected PrimitiveType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends PrimitiveType> immediateSupertypes
  ) {
    super(name, immediateSupertypes);
  }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends PrimitiveType> immediateSupertypes() {
    return (List<? extends PrimitiveType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends PrimitiveType> supertypes() {
    return (Collection<? extends PrimitiveType>) super.supertypes();
  }


  public interface Raw extends DatumType.Raw {}

}
