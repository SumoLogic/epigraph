package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathTagProjectionEntry extends AbstractTagProjectionEntry<ReqPathModelProjection<?, ?>> {
  public ReqPathTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull ReqPathModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
