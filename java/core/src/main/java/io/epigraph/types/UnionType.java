/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class UnionType extends Type {

  protected UnionType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends UnionType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.UNION; }

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

  public @NotNull DataType dataType(boolean polymorphic, @Nullable Tag defaultTag) {
    return new DataType(polymorphic, this, checkTagIsKnown(defaultTag));
  }

  public @Nullable Tag checkTagIsKnown(@Nullable Tag tag) {
    // TODO check it is our/compatible tag (not just same name)?
    if (tag != null && !tagsMap().containsKey(tag.name)) throw new IllegalArgumentException("TODO " + tag.name);
    return tag;
  }

  // TODO .Raw

  public static abstract class Static<MyImmData extends Data.Imm.Static, MyMutData extends Data.Mut.Static<MyImmData>>
      extends UnionType implements Type.Static<MyImmData, MyMutData> {

    private final @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends UnionType.Static> immediateSupertypes,
        @NotNull Function<Data.Mut.@NotNull Raw, @NotNull MyMutData> mutDataConstructor
    ) {
      super(name, immediateSupertypes);
      this.mutDataConstructor = mutDataConstructor;
    }

    @Override
    public final @NotNull MyMutData createDataBuilder() { return mutDataConstructor.apply(new Data.Mut.Raw(this)); }

  }


}
