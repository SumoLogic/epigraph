package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GPrimitiveDatum extends GDatum {
  @NotNull
  private final Object value;

  public GPrimitiveDatum(@Nullable Fqn typeRef, @NotNull Object value, @NotNull TextLocation location) {
    super(typeRef, location);
    this.value = value;
  }

  @NotNull
  public Object value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GPrimitiveDatum that = (GPrimitiveDatum) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), value);
  }

  @Override
  public String toString() {
    String valueString;

    if (value instanceof String) valueString = "\"" + value + '"';
    else valueString = value.toString();

    if (typeRef() == null) return valueString;
    else return typeRef() + "@" + valueString;
  }
}
