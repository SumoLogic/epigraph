package ws.epigraph.service.operations;

import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationRequest {
  @Nullable
  private final ReqFieldPath path;
  @NotNull
  private final ReqOutputFieldProjection outputProjection;

  protected OperationRequest(
      final @Nullable ReqFieldPath path,
      final @NotNull ReqOutputFieldProjection outputProjection) {

    this.path = path;
    this.outputProjection = outputProjection;
  }

  @Nullable
  public ReqFieldPath path() { return path; }

  @NotNull
  public ReqOutputFieldProjection outputProjection() { return outputProjection; }
}
