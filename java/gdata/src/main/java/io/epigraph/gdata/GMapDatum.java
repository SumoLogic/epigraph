package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GMapDatum extends GDatum {
  @NotNull
  private final LinkedHashMap<GDatum, GDataValue> entries;

  public GMapDatum(@Nullable Fqn typeRef,
                   @NotNull LinkedHashMap<GDatum, GDataValue> entries,
                   @NotNull TextLocation location) {

    super(typeRef, location);
    this.entries = entries;
  }

  @NotNull
  public LinkedHashMap<GDatum, GDataValue> entries() {
    return entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GMapDatum gMapDatum = (GMapDatum) o;
    return Objects.equals(entries, gMapDatum.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('(');
    sb.append(entries.entrySet()
                     .stream()
                     .map(e -> e.getKey() + ": " + e.getValue())
                     .collect(Collectors.joining(", ")));
    sb.append(')');
    return sb.toString();
  }
}
