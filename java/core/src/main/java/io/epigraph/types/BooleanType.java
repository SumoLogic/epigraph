/* Created by yegor on 9/6/16. */

package io.epigraph.types;

import io.epigraph.data.BooleanDatum;
import io.epigraph.data.Data;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class BooleanType extends PrimitiveType<Boolean> {

  protected BooleanType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes() {
    return (List<? extends BooleanType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends BooleanType> supertypes() {
    return (Collection<? extends BooleanType>) super.supertypes();
  }

  public abstract @NotNull BooleanDatum.Builder createBuilder(@NotNull Boolean val);


  public static final class Raw extends BooleanType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull BooleanDatum.Builder.Raw createBuilder(@NotNull Boolean val) {
      return new BooleanDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Builder.Raw createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends BooleanDatum.Imm.Static,
      MyDatumBuilder extends BooleanDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends BooleanType implements
      PrimitiveType.Static<Boolean, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<BooleanDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends BooleanType> immediateSupertypes,
        @NotNull Function<BooleanDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Boolean val) {
      return datumBuilderConstructor.apply(new BooleanDatum.Builder.Raw(this, val));
    }

    @Override
    public final @NotNull MyValBuilder createValueBuilder() {
      return valBuilderConstructor.apply(new Val.Builder.Raw(this));
    }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() {
      return dataBuilderConstructor.apply(new Data.Builder.Raw(this));
    }

  }


}
