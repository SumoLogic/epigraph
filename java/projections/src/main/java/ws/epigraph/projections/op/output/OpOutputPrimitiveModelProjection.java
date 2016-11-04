package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection
    extends OpOutputModelProjection<OpOutputPrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<OpOutputPrimitiveModelProjection, PrimitiveType<?>> {

  public OpOutputPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
  }
}
