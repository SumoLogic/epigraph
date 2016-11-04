package ws.epigraph.projections.op.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

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
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
  }
}
