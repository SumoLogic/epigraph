package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericTagProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputTagProjection extends GenericTagProjection<OpOutputModelProjection<?>> {
  public OpOutputTagProjection(@NotNull OpOutputModelProjection<?> projection, @NotNull TextLocation location) {
    super(projection, location);
  }
}
