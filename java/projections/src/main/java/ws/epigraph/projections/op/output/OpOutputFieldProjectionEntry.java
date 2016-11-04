package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjectionEntry extends AbstractFieldProjectionEntry<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputFieldProjection
    > {
  public OpOutputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpOutputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
