package ws.epigraph.gdata;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GDatum extends GDataValue {
  @Nullable
  private final TypeRef typeRef;

  protected GDatum(@Nullable TypeRef typeRef, @NotNull TextLocation location) {
    super(location);
    this.typeRef = typeRef;
  }

  public @Nullable TypeRef typeRef() { return typeRef; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GDatum that = (GDatum) o;
    return Objects.equals(typeRef, that.typeRef);
  }

  @Override
  public int hashCode() { return Objects.hash(typeRef); }
}

