package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractTagProjectionEntry;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpTagPath extends AbstractTagProjectionEntry<OpModelPath<?, ?>> {
  public OpTagPath(
      @NotNull Type.Tag tag,
      @NotNull OpModelPath<?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }
}
