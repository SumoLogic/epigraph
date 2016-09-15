/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.StringDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class StringType extends PrimitiveType<String> {

  protected StringType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends StringType> immediateSupertypes
  ) {
    super(name, immediateSupertypes);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends StringType> immediateSupertypes() {
    return (List<? extends StringType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends StringType> supertypes() {
    return (Collection<? extends StringType>) super.supertypes();
  }

  public abstract @NotNull StringDatum.Builder createBuilder(@NotNull String val);


  public static final class Raw extends StringType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends StringType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull StringDatum.Builder.Raw createBuilder(@NotNull String val) {
      return new StringDatum.Builder.Raw(this, val);
    }

    @Override
    public @NotNull Val.Builder.Raw createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends StringDatum.Imm.Static,
      MyDatumBuilder extends StringDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends StringType implements
      PrimitiveType.Static<String, MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<StringDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends StringType> immediateSupertypes,
        @NotNull Function<StringDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull String val) {
      return datumBuilderConstructor.apply(new StringDatum.Builder.Raw(this, val));
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
