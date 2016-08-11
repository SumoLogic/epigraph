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
import java.util.function.Supplier;

public abstract class IntegerType extends PrimitiveType {

  protected IntegerType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes, polymorphic);
  }

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

  public abstract @NotNull IntegerDatum.Mut createBuilder(@NotNull Integer val);


  public static final class Raw extends IntegerType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends IntegerType> immediateSupertypes,
        boolean polymorphic
    ) {
      super(name, immediateSupertypes, polymorphic);
    }

    @Override
    public @NotNull IntegerDatum.Mut.Raw createBuilder(@NotNull Integer val) {
      return new IntegerDatum.Mut.Raw(this, val);
    }

    @Override
    protected @NotNull Supplier<@NotNull ListType> listTypeSupplier() { return () -> new AnonListType.Raw(false, this); }

    @Override
    public @NotNull Val.Mut.Raw createMutableValue() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut.Raw createMutableData() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends IntegerDatum.Imm.Static,
      MyMutDatum extends IntegerDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends IntegerType implements DatumType.Static<
      IntegerType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<IntegerDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends IntegerType> immediateSupertypes,
        boolean polymorphic,
        @NotNull Function<IntegerDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes, polymorphic);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder(@NotNull Integer val) {
      return mutDatumConstructor.apply(new IntegerDatum.Mut.Raw(this, val));
    }

    @Override
    public final @NotNull MyMutVal createMutableValue() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
