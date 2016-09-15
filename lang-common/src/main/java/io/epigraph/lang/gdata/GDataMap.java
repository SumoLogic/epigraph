package io.epigraph.lang.gdata;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataMap extends GDataVarValue {
  @NotNull
  private final LinkedHashMap<GDataVarValue, GDataValue> entries;

  public GDataMap(@Nullable Fqn typeRef, @NotNull LinkedHashMap<GDataVarValue, GDataValue> entries) {
    super(typeRef);
    this.entries = entries;
  }

  @NotNull
  public LinkedHashMap<GDataVarValue, GDataValue> entries() {
    return entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GDataMap gDataMap = (GDataMap) o;
    return Objects.equals(entries, gDataMap.entries);
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
