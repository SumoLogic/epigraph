/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.names.QualifiedTypeName;
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

  public @NotNull DataType dataType(@Nullable Tag defaultTag) {
    return new DataType(this, checkTagIsKnown(defaultTag));
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
