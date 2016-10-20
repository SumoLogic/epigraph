package io.epigraph.projections.op.output;

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
public class OpOutputVarProjection extends GenericVarProjection<OpOutputTagProjectionEntry, OpOutputVarProjection> {
  public OpOutputVarProjection(@NotNull Type type,
                               @NotNull LinkedHashMap<String, OpOutputTagProjectionEntry> tagProjections,
                               @Nullable List<OpOutputVarProjection> polymorphicTails,
                               @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }
}
