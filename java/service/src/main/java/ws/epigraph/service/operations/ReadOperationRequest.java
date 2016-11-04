package ws.epigraph.service.operations;

import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRequest extends OperationRequest {
  // if path != null then output projection = var projection without field params and annotations
  public ReadOperationRequest(
      final @Nullable ReqFieldPath path,
      final @NotNull ReqOutputFieldProjection outputProjection) {
    super(path, outputProjection);
  }
}
