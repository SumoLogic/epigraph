/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.AnonListTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AnonListType extends ListType {

  protected AnonListType(
      @NotNull List<@NotNull ? extends AnonListType> immediateSupertypes,
      @NotNull DataType elementDataType
  ) { super(new AnonListTypeName(elementDataType.name), immediateSupertypes, elementDataType); }

  @Override
  public @NotNull AnonListTypeName name() { return (AnonListTypeName) super.name(); }


  public static final class Raw extends AnonListType implements ListType.Raw {

    public Raw(@NotNull DataType elementDataType) { super(immediateSupertypes(elementDataType), elementDataType); }

    private static @NotNull List<@NotNull ? extends AnonListType.Raw> immediateSupertypes(@NotNull DataType elementDataType) {
      // FIXME too many new raw types
      return elementDataType.type.immediateSupertypes().stream().map(st -> new AnonListType.Raw(new DataType(
          st,
          defaultTag(st, elementDataType.defaultTag)
      ))).collect(Collectors.toList());
    }

    private static @Nullable Tag defaultTag(Type type, @Nullable Tag tag) {
      return tag == null ? null : type.tagsMap().get(tag.name);
    }

    @Override
    public @NotNull ListDatum.Builder createBuilder() { return new ListDatum.Builder.Raw(this); }

    @Override
    public @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull) {
      return Val.Imm.Raw.create(errorOrNull);
    }

    @Override
    public @NotNull Data.Builder createDataBuilder() { return new Data.Builder.Raw(this); }

  }


  public static abstract class Static<
      MyImmDatum extends ListDatum.Imm.Static,
      MyDatumBuilder extends ListDatum.Builder.Static<MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends AnonListType
      implements ListType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyBuilderVal, MyImmData, MyDataBuilder> {

    private final @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor;

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull List<@NotNull ? extends AnonListType.Static<
            ?,// super MyImmDatum,
            ?,// extends ListDatum.Mut.Static<? super MyImmDatum>,
            ?,// super MyImmVal,
            ?,// extends Val.Mut.Static<? super MyImmVal, ? extends ListDatum.Mut.Static<? super MyImmDatum>>,
            ?,// super MyImmData,
            ? // extends Data.Mut.Static<? super MyImmData>
            >> immediateSupertypes,
        @NotNull DataType elementDataType,
        @NotNull Function<ListDatum.Builder.@NotNull Raw, @NotNull MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(immediateSupertypes, elementDataType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.immValConstructor = immValConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder() {
      return datumBuilderConstructor.apply(new ListDatum.Builder.Raw(this));
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
