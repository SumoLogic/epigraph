package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericTagProjectionEntry<MP extends GenericModelProjection<?>> {
  @NotNull
  private final Type.Tag tag;
  @NotNull
  private final MP projection;
  @NotNull
  private final TextLocation location;

  public GenericTagProjectionEntry(@NotNull Type.Tag tag, @NotNull MP projection, @NotNull TextLocation location) {
    this.tag = tag;
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  public Type.Tag getTag() { return tag; }

  @NotNull
  public MP projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericTagProjectionEntry that = (GenericTagProjectionEntry) o;
    return Objects.equals(tag.name(), that.tag.name()) && Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(tag.name(), projection); }
}
