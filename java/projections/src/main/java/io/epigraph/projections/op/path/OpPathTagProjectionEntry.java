package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathTagProjectionEntry extends AbstractTagProjectionEntry<OpPathModelProjection<?, ?>> {
  public OpPathTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpPathModelProjection<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
