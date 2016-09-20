package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GNullDatum extends GDatum {
  public static GNullDatum NO_TYPE_INSTANCE = new GNullDatum(null);

  public GNullDatum(@Nullable Fqn typeRef) { super(typeRef); }

  @Override
  public String toString() {
    if (typeRef() == null) return "null";
    else return typeRef() + "@null";
  }
}
