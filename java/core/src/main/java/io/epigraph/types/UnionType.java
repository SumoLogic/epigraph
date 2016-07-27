/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class UnionType extends Type {

  public UnionType(@NotNull QualifiedTypeName name, @NotNull List<UnionType> immediateSupertypes) {
    super(name, immediateSupertypes);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<? extends UnionType> immediateSupertypes() {
    return (List<? extends UnionType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<? extends UnionType> supertypes() {
    return (Collection<? extends UnionType>) super.supertypes();
  }


//  public static class Dynamic extends UnionType { // TODO introduce builder instead
//
//    private final List<Tag> immediateTags = new ArrayList<>();
//
//    public Dynamic(QualifiedTypeName name, List<UnionType> immediateSupertypes) {
//      super(name, immediateSupertypes);
//    }
//
//    @Override
//    @NotNull
//    public List<Tag> immediateTags() {
//      return immediateTags;
//    }
//
//    @Override
//    public @NotNull List<Tag> tags() {
//      return null; // TODO track mods (parent mods, too?), re-compute for every mod? don't cache? remove the method?
//    }
//
//    public UnionType.Dynamic addImmediateTag(Tag tag) {
//      immediateTags.add(tag); // TODO check duplicates etc.
//      return this;
//    }
//
//    // TODO seal()?
//
//  }

}
