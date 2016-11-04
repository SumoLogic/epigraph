package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpTagPath extends AbstractTagProjectionEntry<OpModelPath<?, ?>> {
  public OpTagPath(
      @NotNull Type.Tag tag,
      @NotNull OpModelPath<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
