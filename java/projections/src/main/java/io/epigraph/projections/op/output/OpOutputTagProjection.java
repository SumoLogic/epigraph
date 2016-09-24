package io.epigraph.projections.op.output;

import io.epigraph.projections.generic.GenericTagProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputTagProjection extends GenericTagProjection<OpOutputModelProjection<?>> {
  public OpOutputTagProjection(@NotNull Type.Tag tag, @NotNull OpOutputModelProjection<?> projection) {
    super(tag, projection);
  }
}
