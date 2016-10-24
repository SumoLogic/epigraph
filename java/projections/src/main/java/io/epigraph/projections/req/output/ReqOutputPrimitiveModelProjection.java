package io.epigraph.projections.req.output;

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
public class ReqOutputPrimitiveModelProjection
    extends ReqOutputModelProjection<ReqOutputPrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<ReqOutputPrimitiveModelProjection, PrimitiveType<?>> {

  public ReqOutputPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
  }
}
