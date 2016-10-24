package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathFieldProjectionEntry extends AbstractFieldProjectionEntry<
    ReqPathVarProjection,
    ReqPathTagProjectionEntry,
    ReqPathModelProjection<?, ?>,
    ReqPathFieldProjection
    > {
  public ReqPathFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqPathFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
