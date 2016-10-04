package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericTagProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputTagProjection extends GenericTagProjection<ReqOutputModelProjection<?>> {
  public ReqOutputTagProjection(@NotNull ReqOutputModelProjection<?> projection, @NotNull TextLocation location) {
    super(projection, location);
  }
}
