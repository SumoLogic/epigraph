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
    public @NotNull ListDatum.Mut createBuilder() { return new ListDatum.Mut.Raw(this); }

    @Override
    public @NotNull Val.Mut createValueBuilder() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut createDataBuilder() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends ListDatum.Imm.Static,
      MyMutDatum extends ListDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends AnonListType
      implements ListType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData> {

    private final @NotNull Function<ListDatum.Mut.Raw, MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor;

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
        @NotNull Function<ListDatum.Mut.Raw, MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor
    ) {
      super(immediateSupertypes, elementDataType);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder() { return mutDatumConstructor.apply(new ListDatum.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutVal createValueBuilder() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createDataBuilder() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
