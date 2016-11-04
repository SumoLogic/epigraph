package ws.epigraph.projections.req.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPrimitiveModelPath
    extends ReqModelPath<ReqPrimitiveModelPath, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<ReqPrimitiveModelPath, PrimitiveType<?>> {

  public ReqPrimitiveModelPath(
      @NotNull PrimitiveType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
  }
}
