package ws.epigraph.projections.req.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFieldPathEntry extends AbstractFieldProjectionEntry<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>,
    ReqFieldPath
    > {
  public ReqFieldPathEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqFieldPath projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
