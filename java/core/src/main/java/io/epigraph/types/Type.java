/* Created by yegor on 7/21/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.Val;
import io.epigraph.names.TypeName;
import io.epigraph.util.LazyInitializer;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;


public abstract class Type { // TODO split into interface and impl

  private final @NotNull TypeName name;

  private final @NotNull List<@NotNull ? extends Type> immediateSupertypes;

  public final boolean polymorphic;

  protected Type(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends Type> immediateSupertypes,
      boolean polymorphic
  ) {
    this.name = name;
    this.immediateSupertypes = Unmodifiable.list(immediateSupertypes);
    this.polymorphic = polymorphic;

    // assert none of the immediate supertypes is a supertype of another one
    if (immediateSupertypes.stream().anyMatch(is -> is.supertypes().stream().anyMatch(immediateSupertypes::contains)))
      throw new IllegalArgumentException();
  }

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

  private final LazyInitializer<ListType> listOf = new LazyInitializer<>(listOfTypeSupplier()); // FIXME race?

  protected abstract @NotNull Supplier<ListType> listOfTypeSupplier(); // e.g. () -> new AnonListType(false, this)

  public ListType listOf() { return listOf.get(); }

  public abstract @NotNull List<Tag> immediateTags();

  public abstract @NotNull List<Tag> tags(); // FIXME do we need this method?

  public abstract @NotNull Data.Mut createMutableData(); // { return new Data.Mut(this); }


  public interface Raw {}


  public interface Static<MyType extends Type & Type.Static<MyType>> {}


  public static class Tag {

    public final @NotNull String name;

    public final @NotNull DatumType type;

    public Tag(@NotNull String name, @NotNull DatumType type) {
      this.name = name;
      this.type = type;
    }

    public @NotNull Val.Mut createMutableValue() { return this.type.createMutableValue(); }

  }

//  public static interface Tagged { // TODO remove?
//
//    public Tag tag();
//
//  }

}
