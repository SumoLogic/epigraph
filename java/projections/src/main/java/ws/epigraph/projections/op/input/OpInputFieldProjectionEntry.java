package ws.epigraph.projections.op.input;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjectionEntry extends AbstractFieldProjectionEntry<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?,?>,
    OpInputFieldProjection
    > {
  public OpInputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpInputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
