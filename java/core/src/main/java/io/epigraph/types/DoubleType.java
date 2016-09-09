/* Created by yegor on 9/6/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.DoubleDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class DoubleType extends PrimitiveType {

  protected DoubleType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes() {
    return (List<? extends DoubleType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends DoubleType> supertypes() {
    return (Collection<? extends DoubleType>) super.supertypes();
  }

  public abstract @NotNull DoubleDatum.Mut createBuilder(@NotNull Double val);


  public static final class Raw extends DoubleType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends DoubleType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull DoubleDatum.Mut.Raw createBuilder(@NotNull Double val) {
      return new DoubleDatum.Mut.Raw(this, val);
    }

    @Override
    public @NotNull Val.Mut.Raw createValueBuilder() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut.Raw createDataBuilder() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends DoubleDatum.Imm.Static,
      MyMutDatum extends DoubleDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends DoubleType implements DatumType.Static<
      DoubleType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<DoubleDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends DoubleType> immediateSupertypes,
        @NotNull Function<DoubleDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder(@NotNull Double val) {
      return mutDatumConstructor.apply(new DoubleDatum.Mut.Raw(this, val));
    }

    @Override
    public final @NotNull MyMutVal createValueBuilder() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createDataBuilder() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
