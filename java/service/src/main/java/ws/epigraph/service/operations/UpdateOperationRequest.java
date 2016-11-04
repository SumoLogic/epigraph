package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateOperationRequest extends OperationRequest {
  @NotNull
  private final Data data;
  @NotNull
  private final ReqUpdateFieldProjection updateProjection;

  protected UpdateOperationRequest(
      final @Nullable ReqFieldPath path,
      final @NotNull Data data,
      final @NotNull ReqUpdateFieldProjection updateProjection,
      final @NotNull ReqOutputFieldProjection outputProjection) {

    super(path, outputProjection);
    this.data = data;
    this.updateProjection = updateProjection;
  }

  @NotNull
  public ReqUpdateFieldProjection getUpdateProjection() { return updateProjection; }

  @NotNull
  public Data data() { return data; }
}
