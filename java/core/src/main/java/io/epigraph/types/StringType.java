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
import java.util.function.Supplier;

public abstract class StringType extends PrimitiveType {

  protected StringType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends StringType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes, polymorphic);
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

  public abstract @NotNull StringDatum.Mut createMutableDatum(@NotNull String val);


  public static final class Raw extends StringType implements PrimitiveType.Raw {

    protected Raw(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends StringType> immediateSupertypes,
        boolean polymorphic
    ) { super(name, immediateSupertypes, polymorphic); }

    @Override
    public @NotNull StringDatum.Mut.Raw createMutableDatum(@NotNull String val) {
      return new StringDatum.Mut.Raw(this, val);
    }

    @Override
    protected @NotNull Supplier<@NotNull ListType> listOfTypeSupplier() {
      return () -> new AnonListType.Raw(false, this);
    }

    @Override
    public @NotNull Val.Mut.Raw createMutableValue() { return new Val.Mut.Raw(this); }

    @Override
    public @NotNull Data.Mut.Raw createMutableData() { return new Data.Mut.Raw(this); }

  }


  public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
      MyImmDatum extends StringDatum.Imm.Static,
      MyMutDatum extends StringDatum.Mut.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyMutVal extends Val.Mut.Static<MyImmVal, MyMutDatum>,
      MyImmData extends Data.Imm.Static,
      MyMutData extends Data.Mut.Static<MyImmData>
      > extends StringType implements DatumType.Static<
      StringType.Static<MyImmDatum, MyMutDatum, MyImmVal, MyMutVal, MyImmData, MyMutData>
      > {

    private final @NotNull Function<StringDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor;

    private final @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor;

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends StringType> immediateSupertypes,
        boolean polymorphic,
        @NotNull Function<StringDatum.Mut.@NotNull Raw, @NotNull MyMutDatum> mutDatumConstructor,
        @NotNull Function<Val.Mut.@NotNull Raw, @NotNull MyMutVal> mutValConstructor,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes, polymorphic);
      this.mutDatumConstructor = mutDatumConstructor;
      this.mutValConstructor = mutValConstructor;
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutDatum createMutableDatum(@NotNull String val) {
      return mutDatumConstructor.apply(new StringDatum.Mut.Raw(this, val));
    }

    @Override
    public final @NotNull MyMutVal createMutableValue() { return mutValConstructor.apply(new Val.Mut.Raw(this)); }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
