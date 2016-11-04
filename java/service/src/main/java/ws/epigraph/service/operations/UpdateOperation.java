package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.idl.operations.UpdateOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class UpdateOperation<D extends Data> extends Operation<
    UpdateOperationIdl,
    UpdateOperationRequest,
    ReadOperationResponse<D>> {

  protected UpdateOperation(final UpdateOperationIdl declaration) {
    super(declaration);
  }
}
