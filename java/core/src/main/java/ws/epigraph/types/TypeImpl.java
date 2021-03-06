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

/* Created by yegor on 7/21/16. */

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.data.Data;
import ws.epigraph.names.TypeName;
import ws.epigraph.util.Unmodifiable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class TypeImpl implements Type {

  private final @NotNull TypeName name;

  private final @NotNull List<@NotNull ? extends Type> immediateSupertypes;

  private @Nullable Collection<@NotNull ? extends Tag> tags = null;

  private @Nullable Map<@NotNull String, @NotNull ? extends Tag> tagsMap = null;

  private final @NotNull Annotations annotations;

  TypeImpl(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends Type> immediateSupertypes,
      @NotNull Annotations annotations) {
    this.name = name;
    this.immediateSupertypes = Unmodifiable.list(immediateSupertypes); // TODO defensive copy?
    this.annotations = annotations;

    // assert none of the immediate supertypes is a supertype of another one
    if (immediateSupertypes.stream().anyMatch(is -> is.supertypes().stream().anyMatch(immediateSupertypes::contains)))
      throw new IllegalArgumentException();
  }

  @Override
  public @NotNull TypeName name() { return name; }

  /**
   * @return immediate (i.e. not transitive) supertypes of this type
   */
  @Override
  public @NotNull List<@NotNull ? extends Type> immediateSupertypes() { return immediateSupertypes; }

  private @Nullable List<? extends Type> supertypes = null;

  /**
   * @return linearized supertypes of this type, in order of decreasing priority
   */
  @Override
  public @NotNull List<@NotNull ? extends Type> supertypes() {
    if (supertypes == null) { // TODO move initialization to constructor?
      LinkedList<Type> acc = new LinkedList<>();
      for (Type is : immediateSupertypes) {
        assert !acc.contains(is);
        acc.addFirst(is);
        is.supertypes().stream().filter(iss -> !acc.contains(iss)).forEachOrdered(acc::addLast);
      }
      supertypes = Unmodifiable.list(acc);
      assert supertypes.size() == acc.size(); // assert there was no duplicates in the acc
    }
    return supertypes;
  }

  /** @see Class#isInstance(Object) */
  @Override
  public boolean isInstance(@Nullable Data data) { return data != null && isAssignableFrom(data.type()); }

  @Override
  public final @NotNull Collection<@NotNull ? extends Tag> tags() {
    // TODO produce better ordering of the tags (i.e. supertypes first, in the order of supertypes and their tags declaration)
    if (tags == null) { // TODO move initialization to constructor (if possible?)
      LinkedList<Tag> acc = new LinkedList<>(immediateTags());
      for (Type st : supertypes()) {
        st.tags().stream().filter(sf ->
            acc.stream().noneMatch(af -> af.name.equals(sf.name))
        ).forEachOrdered(acc::add);
      }
      tags = Unmodifiable.collection(new LinkedHashSet<>(acc));
      assert tags.size() == acc.size(); // assert there was no duplicates in the acc
    }
    return tags;
  }

  @Override
  public final @NotNull Map<@NotNull String, @NotNull ? extends Tag> tagsMap() {
    if (tagsMap == null) tagsMap = Unmodifiable.map(tags(), t -> t.name, t -> t);
    return tagsMap;
  }

  @Override
  public @NotNull DataTypeApi dataType() { return new DataType(this, null); }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @Override
  public <D extends Data> @NotNull D checkAssignable(@NotNull D data)
      throws IllegalArgumentException { // TODO accept nulls?

    if (data == null)
      throw new IllegalArgumentException(String.format("Can't check if null is compatible with type '%s'", name()));
    if (!isInstance(data))
      throw new IllegalArgumentException(
          String.format("Type '%s' is not compatible with type '%s'", data.type().name(), name())
      );
    return data;
  }

  /** Ensures specified type is a subtype of this type. */
  @Override
  public <T extends Type> T checkAssignable(@NotNull T type) throws IllegalArgumentException {
    if (!isAssignableFrom(type))
      throw new IllegalArgumentException(
          String.format("Type '%s' is not compatible with type '%s'", type.name(), name())
      );
    return type;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof TypeImpl) {
      TypeImpl other = (TypeImpl) obj;
      return name().equals(other.name());
    } else
      return false;
  }

  @Override
  public int hashCode() { return name().hashCode(); }

}
