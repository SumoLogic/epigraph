package io.epigraph.gdata;

import io.epigraph.lang.Fqn;
import io.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GNullDatum extends GDatum {
  public GNullDatum(@Nullable Fqn typeRef, @NotNull TextLocation location) { super(typeRef, location); }

  @Override
  public String toString() {
    if (typeRef() == null) return "null";
    else return typeRef() + "@null";
  }
}