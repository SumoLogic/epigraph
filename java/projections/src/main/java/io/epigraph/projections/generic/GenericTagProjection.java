package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericTagProjection<MP extends GenericModelProjection<?>> {
  @NotNull
  private final Type.Tag tag;
  @NotNull
  private final MP projection;
  @NotNull
  private final TextLocation location;

  public GenericTagProjection(@NotNull Type.Tag tag, @NotNull MP projection, @NotNull TextLocation location) {
    this.tag = tag;
    this.projection = projection;
    this.location = location;

    if (!tag.type.equals(projection.model)) { // or can it be a sub-type?
      throw new IllegalArgumentException(
          String.format("Tag model '%s' is different from tag projection model '%s'", tag.type, projection.model)
      );
    }
  }

  @NotNull
  public Type.Tag tag() { return tag; }

  @NotNull
  public MP projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericTagProjection that = (GenericTagProjection) o;
    return Objects.equals(tag, that.tag) &&
           Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(tag); }
}
