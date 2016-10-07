package io.epigraph.service.operations;

import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRequest extends OperationRequest {
  public ReadOperationRequest(@NotNull ReqOutputFieldProjection outputProjection) {
    super(outputProjection);
  }
}
