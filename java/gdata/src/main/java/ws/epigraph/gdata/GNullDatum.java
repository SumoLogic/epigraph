package ws.epigraph.gdata;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GNullDatum extends GDatum {
  public GNullDatum(@Nullable TypeRef typeRef, @NotNull TextLocation location) { super(typeRef, location); }

  @Override
  public String toString() {
    if (typeRef() == null) return "null";
    else return typeRef() + "@null";
  }
}
