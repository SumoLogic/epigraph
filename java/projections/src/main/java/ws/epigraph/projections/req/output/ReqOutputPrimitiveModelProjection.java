package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.PrimitiveType;
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
