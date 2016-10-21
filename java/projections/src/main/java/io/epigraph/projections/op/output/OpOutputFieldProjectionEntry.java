package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjectionEntry extends AbstractFieldProjectionEntry<OpOutputFieldProjection> {
  public OpOutputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpOutputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
