package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenPrimitiveModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeletePrimitiveModelProjection
    extends OpDeleteModelProjection<OpDeletePrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<OpDeletePrimitiveModelProjection, PrimitiveType<?>> {

  public OpDeletePrimitiveModelProjection(
      @NotNull PrimitiveType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpDeletePrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
  }
}
