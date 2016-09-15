package io.epigraph.lang.gdata;

import io.epigraph.lang.Fqn;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GDataNull extends GDataVarValue {
  public static GDataNull NO_TYPE_INSTANCE = new GDataNull(null);

  public GDataNull(@Nullable Fqn typeRef) { super(typeRef); }

  @Override
  public String toString() {
    if (typeRef() == null) return "null";
    else return typeRef() + "(null)";
  }
}
