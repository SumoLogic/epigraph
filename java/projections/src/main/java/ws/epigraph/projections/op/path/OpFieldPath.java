package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpFieldProjection;
import ws.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldPath extends AbstractOpFieldProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>
    > {

  public OpFieldPath(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpVarPath projection,
      @NotNull TextLocation location) {
    super(params, annotations, projection, location);
  }
}
