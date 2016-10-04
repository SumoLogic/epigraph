package io.epigraph.projections.generic;

import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericTagProjection<MP extends GenericModelProjection<?>> {
  @NotNull
  private final MP projection;
  @NotNull
  private final TextLocation location;

  public GenericTagProjection(@NotNull MP projection, @NotNull TextLocation location) {
    this.projection = projection;
    this.location = location;
  }

  @NotNull
  public MP projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericTagProjection that = (GenericTagProjection) o;
    return Objects.equals(projection, that.projection);
  }

  @Override
  public int hashCode() { return Objects.hash(projection); }
}
