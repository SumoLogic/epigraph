package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathKeyProjection {
  @NotNull
  private final OpParams params;
  @NotNull
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public OpPathKeyProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public OpParams params() { return params; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpPathKeyProjection that = (OpPathKeyProjection) o;
    return Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(annotations);
  }
}
