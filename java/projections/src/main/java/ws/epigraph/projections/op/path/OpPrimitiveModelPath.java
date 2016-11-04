package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPrimitiveModelPath
    extends OpModelPath<OpPrimitiveModelPath, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<OpPrimitiveModelPath, PrimitiveType<?>> {

  public OpPrimitiveModelPath(
      @NotNull PrimitiveType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
  }
}
