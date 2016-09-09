/* Created by yegor on 7/21/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.Val;
import io.epigraph.names.TypeName;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class Type { // TODO split into interface and impl

  private final @NotNull TypeName name;

  private final @NotNull List<@NotNull ? extends Type> immediateSupertypes;

  private @Nullable Collection<@NotNull ? extends Tag> tags = null;

  private @Nullable Map<@NotNull String, @NotNull ? extends Tag> tagsMap = null;

  protected Type(@NotNull TypeName name, @NotNull List<@NotNull ? extends Type> immediateSupertypes) {
    this.name = name;
    this.immediateSupertypes = Unmodifiable.list(immediateSupertypes);

    // assert none of the immediate supertypes is a supertype of another one
    if (immediateSupertypes.stream().anyMatch(is -> is.supertypes().stream().anyMatch(immediateSupertypes::contains)))
      throw new IllegalArgumentException();
  }

  public abstract @NotNull TypeKind kind();

  public @NotNull TypeName name() { return name; }

  /**
   * @return immediate (i.e. not transitive) supertypes of this type, in the order of increasing priority
   */
  public @NotNull List<@NotNull ? extends Type> immediateSupertypes() { return immediateSupertypes; }

  private @Nullable Collection<? extends Type> supertypes = null;

  /**
   * @return linearized supertypes of this type, in order of decreasing priority
   */
  public @NotNull Collection<@NotNull ? extends Type> supertypes() {
    if (supertypes == null) { // TODO move initialization to constructor?
      LinkedList<Type> acc = new LinkedList<>();
      for (Type is : immediateSupertypes) {
        assert !acc.contains(is);
        acc.addFirst(is);
        is.supertypes().stream().filter(iss -> !acc.contains(iss)).forEachOrdered(acc::addFirst);
      }
      supertypes = Unmodifiable.collection(new LinkedHashSet<>(acc));
      assert supertypes.size() == acc.size(); // assert there was no duplicates in the acc
    }
    return supertypes;
  }

  public boolean doesExtend(@NotNull Type type) { return this.equals(type) || supertypes().contains(type); }

  public boolean isAssignableFrom(@NotNull Type type) { return type.doesExtend(this); }

  public abstract @NotNull Collection<@NotNull ? extends Tag> immediateTags();

  public abstract @NotNull Data.Mut createDataBuilder();

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

  public final @NotNull Map<@NotNull String, @NotNull ? extends Tag> tagsMap() {
    if (tagsMap == null) tagsMap = Unmodifiable.map(tags(), t -> t.name, t -> t);
    return tagsMap;
  }


  public interface Raw {}


  public interface Static<MyType extends Type & Type.Static<MyType>> {}


  public static class Tag {

    public final @NotNull String name;

    public final @NotNull DatumType type;

    public Tag(@NotNull String name, @NotNull DatumType type) {
      this.name = name;
      this.type = type;
    }

    public @NotNull String name() { return name; }

    public @NotNull Val.Mut createMutableValue() { return this.type.createValueBuilder(); }

  }

}
