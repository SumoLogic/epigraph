package io.epigraph.gdata;

import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataEnum extends GDatum {
  @NotNull
  private final String value;

  public GDataEnum(@NotNull String value, @NotNull TextLocation location) {
    super(null, location);
    this.value = value;
  }

  @NotNull
  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GDataEnum gDataEnum = (GDataEnum) o;
    return Objects.equals(value, gDataEnum.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  @Override
  public String toString() {
    return value;
  }
}
