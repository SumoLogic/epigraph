package ws.epigraph.projections.op.input;

import ws.epigraph.data.PrimitiveDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputPrimitiveModelProjection
    extends OpInputModelProjection<OpInputPrimitiveModelProjection, PrimitiveType<?>, PrimitiveDatum<?>>
    implements GenPrimitiveModelProjection<OpInputPrimitiveModelProjection, PrimitiveType<?>> {

  public OpInputPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      boolean required,
      @Nullable PrimitiveDatum<?> defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, required, defaultValue, annotations, metaProjection, location);
  }
}
