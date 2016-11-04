package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
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
      final @Nullable ReqFieldPath path,
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
