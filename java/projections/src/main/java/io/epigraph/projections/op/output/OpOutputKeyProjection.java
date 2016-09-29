package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputKeyProjection {
  public enum Presence {OPTIONAL, REQUIRED, FORBIDDEN}

  @NotNull
  private final Presence presence;
  @Nullable
  private final OpParams params;
  @Nullable
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public OpOutputKeyProjection(@NotNull Presence presence,
                               @Nullable OpParams params,
                               @Nullable Annotations annotations,
                               @NotNull TextLocation location) {
    this.presence = presence;
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public Presence presence() { return presence; }

  @Nullable
  public OpParams params() { return params; }

  @Nullable
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputKeyProjection that = (OpOutputKeyProjection) o;
    return presence == that.presence && Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(presence, annotations);
  }
}
