package io.epigraph.projections.op.path;

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
public class OpPathPrimitiveModelProjection
    extends OpPathModelProjection<OpPathPrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<OpPathPrimitiveModelProjection, PrimitiveType<?>> {

  public OpPathPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpPathPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
  }
}
