package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericTagProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputTagProjection extends GenericTagProjection<OpInputModelProjection<?, ?>> {
  public OpInputTagProjection(@NotNull OpInputModelProjection<?, ?> projection, @NotNull TextLocation location) {
    super(projection, location);
  }
}
