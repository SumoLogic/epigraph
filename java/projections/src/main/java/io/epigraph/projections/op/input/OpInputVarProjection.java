package io.epigraph.projections.op.input;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputVarProjection extends AbstractVarProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>
    > {
  public OpInputVarProjection(
      @NotNull Type type,
      @NotNull LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections,
      @Nullable List<OpInputVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
  }
}
