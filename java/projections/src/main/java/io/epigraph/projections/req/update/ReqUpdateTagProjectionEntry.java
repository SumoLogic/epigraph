package io.epigraph.projections.req.update;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateTagProjectionEntry extends AbstractTagProjectionEntry<ReqUpdateModelProjection<?, ?>> {
  public ReqUpdateTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull ReqUpdateModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
