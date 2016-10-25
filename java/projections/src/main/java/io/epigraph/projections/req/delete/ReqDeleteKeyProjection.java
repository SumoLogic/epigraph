package io.epigraph.projections.req.delete;

import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteKeyProjection {
  @NotNull
  private final Datum value;
  @NotNull
  private final ReqParams params;
  @NotNull
  private final Annotations annotations;
  @NotNull
  private final TextLocation location;

  public ReqDeleteKeyProjection(@NotNull Datum value,
                                @NotNull ReqParams params,
                                @NotNull Annotations annotations,
                                @NotNull TextLocation location) {
    this.value = value;
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  @NotNull
  public Datum value() { return value; }

  @NotNull
  public ReqParams params() { return params; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqDeleteKeyProjection that = (ReqDeleteKeyProjection) o;
    return Objects.equals(value, that.value) &&
           Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() { return Objects.hash(value, params, annotations); }
}
