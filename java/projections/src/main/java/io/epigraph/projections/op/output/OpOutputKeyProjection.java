package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.CustomParams;
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
  private final CustomParams customParams;
  @NotNull
  private final TextLocation location;

  public OpOutputKeyProjection(@NotNull Presence presence,
                               @Nullable OpParams params,
                               @Nullable CustomParams customParams,
                               @NotNull TextLocation location) {
    this.presence = presence;
    this.params = params;
    this.customParams = customParams;
    this.location = location;
  }

  @NotNull
  public Presence presence() { return presence; }

  @Nullable
  public OpParams params() { return params; }

  @Nullable
  public CustomParams customParams() { return customParams; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputKeyProjection that = (OpOutputKeyProjection) o;
    return presence == that.presence && Objects.equals(customParams, that.customParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(presence, customParams);
  }
}
