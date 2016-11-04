package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateFieldProjectionEntry extends AbstractFieldProjectionEntry<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?>,
    ReqUpdateFieldProjection
    > {
  public ReqUpdateFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqUpdateFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
