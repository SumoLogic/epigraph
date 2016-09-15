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

  public static abstract class Static<
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends UnionType implements Type.Static<MyImmData, MyDataBuilder> {

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends UnionType.Static> immediateSupertypes,
        @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor
    ) {
      super(name, immediateSupertypes);
      this.dataBuilderConstructor = dataBuilderConstructor;
    }

    @Override
    public final @NotNull MyDataBuilder createDataBuilder() {
      return dataBuilderConstructor.apply(new Data.Builder.Raw(this));
    }

  }


}
