package io.epigraph.service.operations;

import io.epigraph.data.Data;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateOperationRequest extends OperationRequest {
  @NotNull
  private final Data data;

  protected CreateOperationRequest(
      final @Nullable ReqVarPath path,
      final @NotNull Data data,
      final @NotNull ReqOutputFieldProjection outputProjection) {
    super(path, outputProjection);
    this.data = data;
  }

  @NotNull
  public Data data() { return data; }
}
