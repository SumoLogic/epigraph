/* Created by yegor on 9/6/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.DoubleDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class DoubleType extends PrimitiveType<Double> {

  protected DoubleType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes() {
    return (List<? extends DoubleType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends DoubleType> supertypes() {
    return (Collection<? extends DoubleType>) super.supertypes();
  }

  public abstract @NotNull DoubleDatum.Builder createBuilder(@NotNull Double val);


  public static final class Raw extends DoubleType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull DoubleDatum.Builder.Raw createBuilder(@NotNull Double val) {
      return new DoubleDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Builder.Raw createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends DoubleDatum.Imm.Static,
      MyDatumBuilder extends DoubleDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends DoubleType implements
      PrimitiveType.Static<Double, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<DoubleDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends DoubleType> immediateSupertypes,
        @NotNull Function<DoubleDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Double val) {
      return datumBuilderConstructor.apply(new DoubleDatum.Builder.Raw(this, val));
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
