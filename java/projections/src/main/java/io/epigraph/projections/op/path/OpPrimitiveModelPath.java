package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.gen.GenPrimitiveModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
      @Nullable OpPrimitiveModelPath metaProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
  }
}
