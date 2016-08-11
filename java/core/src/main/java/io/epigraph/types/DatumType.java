/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Val;
import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DatumType extends Type {

  public final @NotNull Tag self = new Tag("self", this); // TODO rename to tag?

  public final boolean polymorphic;

  private final @NotNull List<@NotNull Tag> immediateTags = Collections.singletonList(self);

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
  public @NotNull List<@NotNull Tag> immediateTags() { return immediateTags; }

  @Override
  public final @NotNull List<@NotNull Tag> tags() { return immediateTags(); }

  public abstract @NotNull Val.Mut createMutableValue(); // { return new Val.Mut(this); } // FIXME


  public interface Raw extends Type.Raw {}


  public interface Static<MyType extends DatumType & DatumType.Static<MyType>> extends Type.Static<MyType> {}


}
