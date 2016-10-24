package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenPrimitiveModelProjection;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathPrimitiveModelProjection
    extends ReqPathModelProjection<ReqPathPrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<ReqPathPrimitiveModelProjection, PrimitiveType<?>> {

  public ReqPathPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqPathPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
  }
}
