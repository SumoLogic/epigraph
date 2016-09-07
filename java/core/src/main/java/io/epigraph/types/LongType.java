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

public abstract class LongType extends PrimitiveType {

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

  public abstract @NotNull LongDatum.Mut createBuilder(@NotNull Long val);


  public static final class Raw extends LongType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends LongType> immediateSupertypes
    ) { super(name, immediateSupertypes); }

    @Override
    public @NotNull LongDatum.Mut.Raw createBuilder(@NotNull Long val) {
      return new LongDatum.Mut.Raw(this, val);
    }

    @Override
    public @NotNull Val.Mut.Raw createMutableValue() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut.Raw createMutableData() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends LongDatum.Imm.Static,
      MyMutDatum extends LongDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends LongType implements DatumType.Static<
      LongType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<LongDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<? extends LongType> immediateSupertypes,
        @NotNull Function<LongDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createBuilder(@NotNull Long val) {
      return mutDatumConstructor.apply(new LongDatum.Mut.Raw(this, val));
    }

    @Override
    public final @NotNull MyMutVal createMutableValue() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
