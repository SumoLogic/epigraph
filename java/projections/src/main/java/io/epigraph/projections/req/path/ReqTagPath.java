package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqTagPath extends AbstractTagProjectionEntry<ReqModelPath<?, ?>> {
  public ReqTagPath(
      @NotNull Type.Tag tag,
      @NotNull ReqModelPath<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
