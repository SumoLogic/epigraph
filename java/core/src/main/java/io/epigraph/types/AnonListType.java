/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
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
      return elementDataType.type.immediateSupertypes().stream().map(st -> new AnonListType.Raw(new DataType(
          elementDataType.polymorphic, // TODO should it be false?
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
    public @NotNull Val.Builder createValueBuilder() { return new Val.Builder.Raw(this); }

    @Override
    public @NotNull Data.Builder createDataBuilder() { return new Data.Builder.Raw(this); }

    @Override
    public @NotNull Data.Mut createMutableData() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends ListDatum.Imm.Static,
      MyDatumBuilder extends ListDatum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyValBuilder extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends AnonListType
      implements ListType.Static<MyImmDatum, MyDatumBuilder, MyImmVal, MyValBuilder, MyImmData, MyDataBuilder> {

    private final @NotNull Function<ListDatum.Builder.Raw, MyDatumBuilder> datumBuilderConstructor;

    private final @NotNull Function<Val.Builder.Raw, MyValBuilder> valBuilderConstructor;

    private final @NotNull Function<Data.Builder.Raw, MyDataBuilder> dataBuilderConstructor;

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
        @NotNull Function<ListDatum.Builder.Raw, MyDatumBuilder> datumBuilderConstructor,
        @NotNull Function<Val.Builder.Raw, MyValBuilder> valBuilderConstructor,
        @NotNull Function<Data.Builder.Raw, MyDataBuilder> dataBuilderConstructor
    ) {
      super(immediateSupertypes, elementDataType);
      this.datumBuilderConstructor = datumBuilderConstructor;
      this.valBuilderConstructor = valBuilderConstructor;
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDatumBuilder createBuilder() { return datumBuilderConstructor.apply(new ListDatum.Builder.Raw(this)); }

    @Override
    public final @NotNull MyValBuilder createValueBuilder() { return valBuilderConstructor.apply(new Val.Builder.Raw(this)); }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() { return dataBuilderConstructor.apply(new Data.Builder.Raw(this)); }

  }


}
