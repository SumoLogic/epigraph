package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjectionEntry extends AbstractFieldProjectionEntry<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputFieldProjection
    > {
  public ReqOutputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqOutputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
