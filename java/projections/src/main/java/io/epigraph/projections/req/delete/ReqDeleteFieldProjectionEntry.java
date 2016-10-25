package io.epigraph.projections.req.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
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
