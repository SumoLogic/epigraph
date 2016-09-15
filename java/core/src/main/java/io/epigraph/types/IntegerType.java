/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.IntegerDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class IntegerType extends PrimitiveType<Integer> {

  protected IntegerType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes() {
    return (List<? extends IntegerType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends IntegerType> supertypes() {
    return (Collection<? extends IntegerType>) super.supertypes();
  }

  public abstract @NotNull IntegerDatum.Builder createBuilder(@NotNull Integer val);


  public static final class Raw extends IntegerType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull IntegerDatum.Builder.Raw createBuilder(@NotNull Integer val) {
      return new IntegerDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Builder.Raw createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends IntegerDatum.Imm.Static,
      MyDatumBuilder extends IntegerDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends IntegerType implements
      PrimitiveType.Static<Integer, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<IntegerDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends IntegerType> immediateSupertypes,
        @NotNull Function<IntegerDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Integer val) {
      return datumBuilderConstructor.apply(new IntegerDatum.Builder.Raw(this, val));
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
