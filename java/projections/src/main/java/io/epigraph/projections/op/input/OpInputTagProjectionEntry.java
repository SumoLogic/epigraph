package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputTagProjectionEntry extends AbstractTagProjectionEntry<OpInputModelProjection<?, ?, ?>> {
  public OpInputTagProjectionEntry(
      @NotNull Type.Tag tag,
      @NotNull OpInputModelProjection<?, ?, ?> projection,
      @NotNull TextLocation location) {

    super(tag, projection, location);
  }
}
