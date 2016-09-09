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

  private final @NotNull Collection<@NotNull ? extends Tag> immediateTags = Collections.singleton(self);

  protected DatumType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends DatumType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

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
  public @NotNull Collection<@NotNull ? extends Tag> immediateTags() { return immediateTags; }

  public @NotNull DataType dataType(boolean polymorphic) { return new DataType(polymorphic, this, self); } // TODO cache

  public abstract @NotNull Val.Mut createValueBuilder();


  public interface Raw extends Type.Raw {}


  public interface Static<MyType extends DatumType & DatumType.Static<MyType>> extends Type.Static<MyType> {}


}
