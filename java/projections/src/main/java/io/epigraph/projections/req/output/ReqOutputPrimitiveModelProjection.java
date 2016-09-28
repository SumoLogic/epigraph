package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.CustomParams;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputPrimitiveModelProjection extends ReqOutputModelProjection<PrimitiveType> {
  public ReqOutputPrimitiveModelProjection(@NotNull PrimitiveType model,
                                           boolean required,
                                           @Nullable ReqParams params,
                                           @Nullable CustomParams customParams,
                                           @Nullable ReqOutputModelProjection<?> metaProjection,
                                           @NotNull TextLocation location) {
    super(model, required, params, customParams, metaProjection, location);
  }
}
