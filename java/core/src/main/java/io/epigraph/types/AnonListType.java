/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.names.AnonListTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AnonListType extends ListType {

  protected AnonListType(boolean polymorphic, @NotNull Type elementType) {
    super(AnonListTypeName.of(polymorphic, elementType.name()), polymorphic, elementType);
  }

  @Override
  public @NotNull AnonListTypeName name() { return (AnonListTypeName) super.name(); }


  public static final class Raw extends AnonListType implements ListType.Raw {

    public Raw(boolean polymorphic, @NotNull Type elementType) { super(polymorphic, elementType); }

    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> new AnonListType.Raw(false, this); }

    @Override
    public @NotNull ListDatum.Mut createBuilder() { return new ListDatum.Mut.Raw(this); }

    @Override
    public @NotNull Val.Mut createMutableValue() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut createMutableData() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends ListDatum.Imm.Static,
      MyMutDatum extends ListDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends AnonListType implements ListType.Static<
      AnonListType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<ListDatum.Mut.Raw, MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor;

    protected Static(
        boolean polymorphic,
        @NotNull Type elementType,
        @NotNull Function<ListDatum.Mut.Raw, MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.Raw, MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.Raw, MyMutData> mutDataConstructor
    ) {
      super(polymorphic, elementType);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder() { return mutDatumConstructor.apply(new ListDatum.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutVal createMutableValue() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

    // should be overridden in (generated) static types that have lists of themselves declared in the schema
    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() { return throwingListTypeSupplier; }

  }


}
