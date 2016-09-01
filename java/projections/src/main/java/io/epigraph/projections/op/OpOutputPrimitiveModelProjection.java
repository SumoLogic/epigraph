package io.epigraph.projections.op;

import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection extends OpOutputModelProjection<PrimitiveType, OpOutputPrimitiveModelProjection> {
  public OpOutputPrimitiveModelProjection(@NotNull PrimitiveType model,
                                          boolean required,
                                          @Nullable Set<OpParam> params,
                                          @Nullable LinkedHashSet<OpOutputPrimitiveModelProjection> polymorphicTail) {
    super(model, required, params, polymorphicTail);
  }
}
