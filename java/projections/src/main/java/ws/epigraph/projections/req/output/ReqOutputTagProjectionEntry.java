package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputTagProjectionEntry extends AbstractTagProjectionEntry<ReqOutputModelProjection<?, ?>> {
  public ReqOutputTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull ReqOutputModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
