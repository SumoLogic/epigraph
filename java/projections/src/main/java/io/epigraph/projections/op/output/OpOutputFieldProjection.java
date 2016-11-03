package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.AbstractOpFieldProjection;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection extends AbstractOpFieldProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>
    > {

  public OpOutputFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpOutputVarProjection projection,
      @NotNull TextLocation location) {
    super(params, annotations, projection, location);
  }
}
