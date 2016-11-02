package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.AbstractOpFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteFieldProjection extends AbstractOpFieldProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>
    > {

  public OpDeleteFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteVarProjection projection,
      @NotNull TextLocation location) {
    super(params, annotations, projection, location);
  }
}
