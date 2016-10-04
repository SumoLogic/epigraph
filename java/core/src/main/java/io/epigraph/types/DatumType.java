/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.Datum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  /** @see Class#isInstance(Object) */
  public boolean isInstance(@Nullable Datum datum) { return datum != null && isAssignableFrom(datum.type()); }

  public <D extends Datum> D checkAssignable(@NotNull D datum) throws IllegalArgumentException { // TODO accept nulls?
    if (!isInstance(datum)) throw new IllegalArgumentException("TODO");
    return datum;
  }

  public @NotNull DataType dataType() { return new DataType(this, self); } // TODO cache

  public abstract @NotNull Val.Imm createValue(@Nullable ErrorValue errorOrNull);


  public interface Raw extends Type.Raw {

    @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull);

  }


  public interface Static<
      MyImmDatum extends Datum.Imm.Static,
      MyDatumBuilder extends Datum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends Type.Static<MyImmData, MyDataBuilder> {

    @Override
    @NotNull MyDataBuilder createDataBuilder();

    @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull);

  }


}
