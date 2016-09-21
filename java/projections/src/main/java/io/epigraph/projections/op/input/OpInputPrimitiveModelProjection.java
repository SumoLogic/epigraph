package io.epigraph.projections.op.input;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputPrimitiveModelProjection extends OpInputModelProjection<PrimitiveType, PrimitiveDatum<?>> {
  public OpInputPrimitiveModelProjection(@NotNull PrimitiveType model,
                                         boolean required,
                                         @Nullable PrimitiveDatum<?> defaultValue,
                                         @Nullable OpCustomParams customParams,
                                         @Nullable OpInputModelProjection<?, ?> metaProjection) {
    super(model, required, defaultValue, customParams, metaProjection);
  }
}
