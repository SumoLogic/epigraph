/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
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

  public abstract @NotNull Val.Builder createValueBuilder();

  // TODO this is needed for mutable universe, which is likely to be raw-only - move to .Raw?
  public /*abstract*/ @NotNull Val.Mut createMutableValue() { throw new UnsupportedOperationException(); }


  public interface Raw extends Type.Raw {

    //@NotNull Val.Mut createMutableValue();

  }


  public interface Static<
      MyImmDatum extends Datum.Imm.Static,
      MyDatumBuilder extends Datum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends Type.Static<MyImmData, MyDataBuilder> {}


}
