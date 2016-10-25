package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteKeyProjection {
  public enum Presence {OPTIONAL, REQUIRED, FORBIDDEN}

  @NotNull
  private final Presence presence;
  @NotNull
  private final OpParams params;
  @NotNull
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public OpDeleteKeyProjection(@NotNull Presence presence,
                               @NotNull OpParams params,
                               @NotNull Annotations annotations,
                               @NotNull TextLocation location) {
    this.presence = presence;
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public Presence presence() { return presence; }

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
    OpDeleteKeyProjection that = (OpDeleteKeyProjection) o;
    return presence == that.presence && Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(presence, annotations);
  }
}
