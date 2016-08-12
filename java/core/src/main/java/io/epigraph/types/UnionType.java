/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class UnionType extends Type {

  protected UnionType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends UnionType> immediateSupertypes,
      boolean polymorphic
  ) {
    super(name, immediateSupertypes, polymorphic);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends UnionType> immediateSupertypes() {
    return (List<? extends UnionType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends UnionType> supertypes() {
    return (Collection<? extends UnionType>) super.supertypes();
  }

  // TODO .Raw

  public static abstract class Static<MyImmData extends Data.Imm.Static, MyMutData extends Data.Mut.Static<MyImmData>>
      extends UnionType implements Type.Static<UnionType.Static<MyImmData, MyMutData>> {

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends UnionType.Static> immediateSupertypes,
        boolean polymorphic,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes, polymorphic);
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutData createMutableData() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
