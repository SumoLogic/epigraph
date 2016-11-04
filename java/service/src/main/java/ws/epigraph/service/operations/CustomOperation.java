package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.idl.operations.CustomOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class CustomOperation<D extends Data> extends Operation<
    CustomOperationIdl,
    CustomOperationRequest,
    ReadOperationResponse<D>> {

  protected CustomOperation(final CustomOperationIdl declaration) {
    super(declaration);
  }
}
