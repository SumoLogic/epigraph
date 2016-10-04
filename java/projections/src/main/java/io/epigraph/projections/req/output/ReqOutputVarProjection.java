package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputVarProjection extends GenericVarProjection<ReqOutputTagProjection, ReqOutputVarProjection> {
  public ReqOutputVarProjection(@NotNull Type type,
                                @NotNull LinkedHashMap<Type.Tag, ReqOutputTagProjection> tagProjections,
                                @Nullable List<ReqOutputVarProjection> polymorphicTails,
                                @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }
}
