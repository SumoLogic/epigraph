package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjectionEntry;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldPathEntry extends AbstractFieldProjectionEntry<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>,
    OpFieldPath
    > {
  public OpFieldPathEntry(
      @NotNull RecordType.Field field,
      @NotNull OpFieldPath projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
