package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
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
