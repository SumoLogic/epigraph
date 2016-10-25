package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractFieldProjectionEntry;
import io.epigraph.types.RecordType;
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
