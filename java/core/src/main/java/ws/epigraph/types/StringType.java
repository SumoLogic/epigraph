/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.data.StringDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class StringType extends PrimitiveType<String> {

  protected StringType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends StringType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

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
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder.Raw createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends StringDatum.Imm.Static,
      MyDatumBuilder extends StringDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends StringType implements
      PrimitiveType.Static<String, MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<StringDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends StringType> immediateSupertypes,
        @NotNull Function<StringDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder(@NotNull String val) {
      return datumBuilderConstructor.apply(new StringDatum.Builder.Raw(this, val));
    }

    @Override
    public final @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull) {
      return immValConstructor.apply(Val.Imm.Raw.create(errorOrNull));
    }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() {
      return dataBuilderConstructor.apply(new Data.Builder.Raw(this));
    }

  }


}
