package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenericVarProjection<T extends GenericTagProjection<?>, S extends GenericVarProjection<T, S>> {
  @NotNull
  private final Type type;
  @NotNull
  private final LinkedHashMap<Type.Tag, T> tagProjections;
  @Nullable
  private final List<S> polymorphicTails;
  @NotNull
  private final TextLocation location;

  public GenericVarProjection(@NotNull Type type,
                              @NotNull LinkedHashMap<Type.Tag, T> tagProjections,
                              @Nullable List<S> polymorphicTails,
                              @NotNull TextLocation location) {
    this.type = type;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    // todo validate tag types == tag projection models
    // todo validate tails (should be subtypes of `type`)
  }

  @NotNull
  public Type type() { return type; }

  public @NotNull LinkedHashMap<Type.Tag, T> tagProjections() { return tagProjections; }

  @Nullable
  public T tagProjection(@NotNull Type.Tag tag) {
    return tagProjections.get(tag);
  }

  public @Nullable List<S> polymorphicTails() { return polymorphicTails; }

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
