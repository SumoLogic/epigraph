package io.epigraph.projections.op;

import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection extends OpOutputModelProjection<PrimitiveType, OpOutputPrimitiveModelProjection> {
  public OpOutputPrimitiveModelProjection(@NotNull PrimitiveType model,
                                          boolean includeInDefault,
                                          @Nullable Set<OpParam> params) {
    super(model, includeInDefault, params);
  }

  @Override
  protected OpOutputPrimitiveModelProjection mergedProjection(@NotNull PrimitiveType model,
                                                              boolean mergedRequired,
                                                              @Nullable Set<OpParam> mergedParams,
                                                              @NotNull Collection<OpOutputPrimitiveModelProjection> projectionsToMerge) {
    return new OpOutputPrimitiveModelProjection(model, mergedRequired, mergedParams);
  }
}
