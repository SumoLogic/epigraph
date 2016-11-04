package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
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
