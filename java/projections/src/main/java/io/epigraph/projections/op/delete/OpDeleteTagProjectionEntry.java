package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteTagProjectionEntry extends AbstractTagProjectionEntry<OpDeleteModelProjection<?, ?>> {
  public OpDeleteTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpDeleteModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
