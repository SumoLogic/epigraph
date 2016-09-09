/* Created by yegor on 9/6/16. */

package io.epigraph.types;

import io.epigraph.data.BooleanDatum;
import io.epigraph.data.Data;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class BooleanType extends PrimitiveType {

  protected BooleanType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes() {
    return (List<? extends BooleanType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends BooleanType> supertypes() {
    return (Collection<? extends BooleanType>) super.supertypes();
  }

  public abstract @NotNull BooleanDatum.Mut createBuilder(@NotNull Boolean val);


  public static final class Raw extends BooleanType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends BooleanType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull BooleanDatum.Mut.Raw createBuilder(@NotNull Boolean val) {
      return new BooleanDatum.Mut.Raw(this, val);
    }

    @Override
    public @NotNull Val.Mut.Raw createValueBuilder() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut.Raw createDataBuilder() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends BooleanDatum.Imm.Static,
      MyMutDatum extends BooleanDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends BooleanType implements DatumType.Static<
      BooleanType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<BooleanDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends BooleanType> immediateSupertypes,
        @NotNull Function<BooleanDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder(@NotNull Boolean val) {
      return mutDatumConstructor.apply(new BooleanDatum.Mut.Raw(this, val));
    }

    @Override
    public final @NotNull MyMutVal createValueBuilder() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createDataBuilder() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
