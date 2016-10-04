package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import io.epigraph.lang.TextLocation;
import io.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GRecordDatum extends GDatum {
  @NotNull
  private final LinkedHashMap<String, GDataValue> fields;

  public GRecordDatum(@Nullable TypeRef typeRef,
                      @NotNull LinkedHashMap<String, GDataValue> fields,
                      @NotNull TextLocation location) {

    super(typeRef, location);
    this.fields = fields;
  }

  @NotNull
  public LinkedHashMap<String, GDataValue> fields() { return fields; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GRecordDatum gDataMap = (GRecordDatum) o;
    return Objects.equals(fields, gDataMap.fields);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), fields); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('{');
    sb.append(fields.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", ")));
    sb.append('}');
    return sb.toString();
  }
}
