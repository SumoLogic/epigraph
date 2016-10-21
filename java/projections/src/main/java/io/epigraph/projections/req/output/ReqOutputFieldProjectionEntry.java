package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjectionEntry extends AbstractFieldProjectionEntry<ReqOutputFieldProjection> {
  public ReqOutputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull ReqOutputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
