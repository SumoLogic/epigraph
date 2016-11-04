package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdatePrimitiveModelProjection
    extends ReqUpdateModelProjection<ReqUpdatePrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<ReqUpdatePrimitiveModelProjection, PrimitiveType<?>> {

  public ReqUpdatePrimitiveModelProjection(
      @NotNull PrimitiveType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    super(model, true, params, annotations, location);
  }
}
