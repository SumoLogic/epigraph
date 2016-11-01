package io.epigraph.service.operations;

import io.epigraph.data.Data;
import io.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationRequest extends OperationRequest {
  @NotNull
  private final Data data;
  @NotNull
  private final ReqDeleteFieldProjection DeleteProjection;

  public DeleteOperationRequest(
      final @Nullable ReqVarPath path,
      final @NotNull Data data,
      final @NotNull ReqDeleteFieldProjection DeleteProjection,
      final @NotNull ReqOutputFieldProjection outputProjection) {

    super(path, outputProjection);
    this.data = data;
    this.DeleteProjection = DeleteProjection;
  }

  @NotNull
  public ReqDeleteFieldProjection getDeleteProjection() { return DeleteProjection; }

  @NotNull
  public Data data() { return data; }
}
