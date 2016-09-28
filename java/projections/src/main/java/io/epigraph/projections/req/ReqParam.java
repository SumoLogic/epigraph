package io.epigraph.projections.req;

import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqParam {
  @NotNull
  public final String name;
  @Nullable
  private final Datum value;
  @NotNull
  private final TextLocation location;

  public ReqParam(@NotNull String name, @Nullable Datum value, @NotNull TextLocation location) {
    this.name = name;
    this.value = value;
    this.location = location;
  }

  @NotNull
  public String name() { return name; }

  @Nullable
  public Datum value() { return value; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqParam reqParam = (ReqParam) o;
    return Objects.equals(name, reqParam.name) && Objects.equals(value, reqParam.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
