package io.epigraph.projections.op.input;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenPrimitiveModelProjection;
import io.epigraph.types.PrimitiveType;
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
