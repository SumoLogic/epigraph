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
public class GenericVarProjection<T extends GenericTagProjectionEntry<?>, S extends GenericVarProjection<T, S>> {
  @NotNull
  private final Type type;
  @NotNull
  private final LinkedHashMap<String, T> tagProjections;
  @Nullable
  private final List<S> polymorphicTails;

  private int polymorphicDepth = -1;
  @NotNull
  private final TextLocation location;

  public GenericVarProjection(@NotNull Type type,
                              @NotNull LinkedHashMap<String, T> tagProjections,
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

  public @NotNull LinkedHashMap<String, T> tagProjections() { return tagProjections; }

  @Nullable
  public T tagProjection(@NotNull String tagName) { return tagProjections.get(tagName); }

  public @Nullable List<S> polymorphicTails() { return polymorphicTails; }

  /** Max polymorphic tail depth. */
  public int polymorphicDepth() {
    if (polymorphicDepth == -1) polymorphicDepth = polymorphicTails == null
        ? 0
        : polymorphicTails.stream().mapToInt(GenericVarProjection::polymorphicDepth).max().orElse(0);
    return polymorphicDepth;
  }

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
