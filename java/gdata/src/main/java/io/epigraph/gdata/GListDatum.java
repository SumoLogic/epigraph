package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GListDatum extends GDatum {
  @NotNull
  private final List<GDataValue> values;

  public GListDatum(@Nullable Fqn typeRef, @NotNull List<GDataValue> values, @NotNull TextLocation location) {
    super(typeRef, location);
    this.values = values;
  }

  @NotNull
  public List<GDataValue> values() {
    return values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GListDatum gListDatum = (GListDatum) o;
    return Objects.equals(values, gListDatum.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('[');
    sb.append(values.stream().map(Object::toString).collect(Collectors.joining(", ")));
    sb.append(']');
    return sb.toString();
  }
}
