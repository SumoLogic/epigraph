package io.epigraph.service.operations;

import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationRequest {
  @NotNull
  private final ReqOutputFieldProjection outputProjection;

  protected OperationRequest(@NotNull ReqOutputFieldProjection outputProjection) {
    this.outputProjection = outputProjection;
  }

  @NotNull
  public ReqOutputFieldProjection outputProjection() { return outputProjection; }
}
