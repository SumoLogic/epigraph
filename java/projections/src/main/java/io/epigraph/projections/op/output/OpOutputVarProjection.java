package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.generic.GenericVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputVarProjection extends GenericVarProjection<OpOutputTagProjection, OpOutputVarProjection> {
  public OpOutputVarProjection(@NotNull Type type,
                               @NotNull LinkedHashSet<OpOutputTagProjection> tagProjections,
                               @Nullable LinkedHashSet<OpOutputVarProjection> polymorphicTails,
                               @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }

  public OpOutputVarProjection(@NotNull Type type,
                               @NotNull TextLocation location,
                               OpOutputTagProjection... tagProjections) {
    this(type, new LinkedHashSet<>(Arrays.asList(tagProjections)), null, location);
  }
}
