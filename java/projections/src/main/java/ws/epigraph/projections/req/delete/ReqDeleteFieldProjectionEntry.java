package ws.epigraph.projections.req.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteFieldProjectionEntry extends AbstractFieldProjectionEntry<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>,
    ReqDeleteFieldProjection
    > {
  public ReqDeleteFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqDeleteFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
