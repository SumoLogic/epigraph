/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DatumType extends Type {

  public final @NotNull Tag self = new Tag("self", this);

  public final boolean polymorphic;

  protected DatumType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends DatumType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes);
    this.polymorphic = polymorphic;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends DatumType> immediateSupertypes() {
    return (List<? extends DatumType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends DatumType> supertypes() {
    return (Collection<? extends DatumType>) super.supertypes();
  }

  @Override
  public @NotNull List<@NotNull Tag> immediateTags() {
    return Collections.singletonList(self); // TODO cache collection itself
  }

  @Override
  public final @NotNull List<@NotNull Tag> tags() {
    return immediateTags();
  }

}
