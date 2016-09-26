package io.epigraph.projections.op;

import io.epigraph.gdata.GDataValue;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpCustomParam {
  @NotNull
  private final String name;
  @NotNull
  private final GDataValue value;
  @NotNull
  private final TextLocation location;

  public OpCustomParam(@NotNull String name, @NotNull GDataValue value, @NotNull TextLocation location) {
    this.name = name;
    this.value = value;
    this.location = location;
  }

  @NotNull
  public String name() { return name; }

  @NotNull
  public GDataValue value() { return value; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpCustomParam that = (OpCustomParam) o;
    return Objects.equals(name, that.name) &&
           Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
