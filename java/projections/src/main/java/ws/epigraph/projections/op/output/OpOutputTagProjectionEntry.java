package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputTagProjectionEntry extends AbstractTagProjectionEntry<OpOutputModelProjection<?, ?>> {
  public OpOutputTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpOutputModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
