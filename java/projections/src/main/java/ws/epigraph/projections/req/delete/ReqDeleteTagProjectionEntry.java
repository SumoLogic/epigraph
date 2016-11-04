package ws.epigraph.projections.req.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteTagProjectionEntry extends AbstractTagProjectionEntry<ReqDeleteModelProjection<?, ?>> {
  public ReqDeleteTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull ReqDeleteModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
