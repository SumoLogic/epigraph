package io.epigraph.service.operations;

import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationRequest {
  @Nullable
  private final ReqVarPath path;
  @NotNull
  private final ReqOutputFieldProjection outputProjection;

  protected OperationRequest(
      final @Nullable ReqVarPath path,
      final @NotNull ReqOutputFieldProjection outputProjection) {

    this.path = path;
    this.outputProjection = outputProjection;
  }

  @Nullable
  public ReqVarPath path() { return path; }

  @NotNull
  public ReqOutputFieldProjection outputProjection() { return outputProjection; }
}
