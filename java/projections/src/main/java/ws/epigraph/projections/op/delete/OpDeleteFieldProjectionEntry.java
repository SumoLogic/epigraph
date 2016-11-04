package ws.epigraph.projections.op.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteFieldProjectionEntry extends AbstractFieldProjectionEntry<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteFieldProjection
    > {
  public OpDeleteFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpDeleteFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
