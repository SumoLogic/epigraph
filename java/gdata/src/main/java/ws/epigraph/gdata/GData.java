package ws.epigraph.gdata;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GData extends GDataValue {
  @Nullable
  private final TypeRef typeRef;
  @NotNull
  private final LinkedHashMap<String, GDatum> tags;

  public GData(@Nullable TypeRef typeRef,
               @NotNull LinkedHashMap<String, GDatum> tags,
               @NotNull TextLocation location) {

    super(location);
    this.typeRef = typeRef;
    this.tags = tags;
  }

  @Nullable
  public TypeRef typeRef() { return typeRef; }

  @NotNull
  public LinkedHashMap<String, GDatum> tags() { return tags; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GData gData = (GData) o;
    return Objects.equals(typeRef, gData.typeRef) &&
           Objects.equals(tags, gData.tags);
  }

  @Override
  public int hashCode() { return Objects.hash(typeRef, tags); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('<');
    sb.append(tags.entrySet()
                  .stream()
                  .map(e -> e.getKey() + ": " + e.getValue())
                  .collect(Collectors.joining(", ")));
    sb.append('>');
    return sb.toString();
  }
}
