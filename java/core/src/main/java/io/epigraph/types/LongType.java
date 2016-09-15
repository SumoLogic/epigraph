/* Created by yegor on 9/6/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.LongDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class LongType extends PrimitiveType<Long> {

  protected LongType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends LongType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends LongType> immediateSupertypes() {
    return (List<? extends LongType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends LongType> supertypes() {
    return (Collection<? extends LongType>) super.supertypes();
  }

  public abstract @NotNull LongDatum.Builder createBuilder(@NotNull Long val);


  public static final class Raw extends LongType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends LongType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull LongDatum.Builder.Raw createBuilder(@NotNull Long val) {
      return new LongDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Builder.Raw createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends LongDatum.Imm.Static,
      MyDatumBuilder extends LongDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends LongType implements
      PrimitiveType.Static<Long, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<LongDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends LongType> immediateSupertypes,
        @NotNull Function<LongDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull Long val) {
      return datumBuilderConstructor.apply(new LongDatum.Builder.Raw(this, val));
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
