/*
 * Copyright 2017 Sumo Logic
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.names.QualifiedTypeName;

import java.util.List;
import java.util.function.Function;

public abstract class EntityType extends TypeImpl implements EntityTypeApi {

  protected EntityType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends EntityType> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.ENTITY; }

  @Override
  public @NotNull QualifiedTypeName name() {
    return (QualifiedTypeName) super.name();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends EntityType> immediateSupertypes() {
    return (List<? extends EntityType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends EntityType> supertypes() {
    return (List<? extends EntityType>) super.supertypes();
  }

  public @NotNull DataType dataType(@Nullable Tag defaultTag) {
    return new DataType(this, checkTagIsKnown(defaultTag));
  }

  @Override
  public @NotNull DataTypeApi dataType(final @Nullable TagApi defaultTag) { return dataType((Tag) defaultTag); }

  public @Nullable Tag checkTagIsKnown(@Nullable Tag tag) {
    // TODO check it is our/compatible tag (not just same name)?
    if (tag != null && !tagsMap().containsKey(tag.name))
      throw new IllegalArgumentException("TODO " + tag.name);
    return tag;
  }

  // TODO .Raw

  public abstract static class Static<
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends EntityType implements Type.Static<MyImmData, MyDataBuilder> {

    private final @NotNull Function<Data.Builder.@NotNull Raw, @NotNull MyDataBuilder> dataBuilderConstructor;

    protected Static(
        @NotNull QualifiedTypeName name,
        @NotNull List<@NotNull ? extends EntityType.Static> immediateSupertypes,
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
