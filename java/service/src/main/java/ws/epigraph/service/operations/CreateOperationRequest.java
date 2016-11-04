package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateOperationRequest extends OperationRequest {
  @NotNull
  private final Data data;

  protected CreateOperationRequest(
      final @Nullable ReqFieldPath path,
      final @NotNull Data data,
      final @NotNull ReqOutputFieldProjection outputProjection) {
    super(path, outputProjection);
    this.data = data;
  }

  @NotNull
  public Data data() { return data; }
}
