package io.epigraph.projections.op.input;

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
public class OpInputVarProjection extends GenericVarProjection<OpInputTagProjection, OpInputVarProjection> {
  public OpInputVarProjection(@NotNull Type type,
                              @NotNull LinkedHashSet<OpInputTagProjection> tagProjections,
                              @Nullable LinkedHashSet<OpInputVarProjection> polymorphicTails,
                              @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }

  public OpInputVarProjection(@NotNull Type type, @NotNull TextLocation location, OpInputTagProjection... tagProjections) {
    this(type, new LinkedHashSet<>(Arrays.asList(tagProjections)), null, location);
  }
}
