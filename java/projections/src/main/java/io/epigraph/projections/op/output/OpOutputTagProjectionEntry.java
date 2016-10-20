package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputTagProjectionEntry extends GenericTagProjectionEntry<OpOutputModelProjection<?>> {
  public OpOutputTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpOutputModelProjection<?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
