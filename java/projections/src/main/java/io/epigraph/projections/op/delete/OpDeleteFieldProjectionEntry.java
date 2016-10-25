package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteFieldProjectionEntry extends AbstractFieldProjectionEntry<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteFieldProjection
    > {
  public OpDeleteFieldProjectionEntry(
      @NotNull RecordType.Field field,
      @NotNull OpDeleteFieldProjection projection,
      @NotNull TextLocation location) {
    super(field, projection, location);
  }
}
