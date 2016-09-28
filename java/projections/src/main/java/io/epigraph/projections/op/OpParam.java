package io.epigraph.projections.op;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.op.input.OpInputModelProjection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParam {
  @NotNull
  private final String name;
  @NotNull
  private final OpInputModelProjection<?, ?> projection;
  @NotNull
  private final TextLocation location;

  public OpParam(@NotNull String name,
                 @NotNull OpInputModelProjection<?, ?> projection,
                 @NotNull TextLocation location) {
    this.name = name;
    this.projection = projection;
    this.location = location;
  }

  // note: custom params are on the model projection

  @NotNull
  public String name() { return name; }

  @NotNull
  public OpInputModelProjection<?, ?> projection() { return projection; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpParam opParam = (OpParam) o;
    return Objects.equals(name, opParam.name) &&
           Objects.equals(projection, opParam.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, projection);
  }
}
