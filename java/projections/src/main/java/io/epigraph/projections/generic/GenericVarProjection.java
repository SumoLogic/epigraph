package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenericVarProjection<T extends GenericTagProjection<?>, S extends GenericVarProjection<T, S>> {
  @NotNull
  private final Type type;
  @NotNull
  private final LinkedHashSet<T> tagProjections;
  @Nullable
  private final LinkedHashSet<S> polymorphicTails;
  @NotNull
  private final TextLocation location;

  public GenericVarProjection(@NotNull Type type,
                              @NotNull LinkedHashSet<T> tagProjections,
                              @Nullable LinkedHashSet<S> polymorphicTails,
                              @NotNull TextLocation location) {
    this.type = type;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    // todo validate tails (should be subtypes of `type`)
  }

  @NotNull
  public Type type() { return type; }

  @NotNull
  public LinkedHashSet<T> tagProjections() { return tagProjections; }

  @Nullable
  public T tagProjection(@NotNull Type.Tag tag) {
    for (T tagProjection : tagProjections)
      if (tagProjection.tag().equals(tag)) return tagProjection;

    return null;
  }

  @Nullable
  public LinkedHashSet<S> polymorphicTails() { return polymorphicTails; }

  @NotNull
  public TextLocation location() {
    return location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericVarProjection that = (GenericVarProjection) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(tagProjections, that.tagProjections) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, tagProjections, polymorphicTails);
  }
}
