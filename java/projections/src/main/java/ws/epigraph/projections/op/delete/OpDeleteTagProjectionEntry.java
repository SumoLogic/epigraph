package ws.epigraph.projections.op.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteTagProjectionEntry extends AbstractTagProjectionEntry<OpDeleteModelProjection<?, ?>> {
  public OpDeleteTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpDeleteModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
