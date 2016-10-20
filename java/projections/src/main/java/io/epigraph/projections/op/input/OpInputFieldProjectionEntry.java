package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericFieldProjectionEntry;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjectionEntry extends GenericFieldProjectionEntry<OpInputFieldProjection> {
  public OpInputFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpInputFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
