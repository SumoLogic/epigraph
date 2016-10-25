package io.epigraph.projections.req.update;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
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
