package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.idl.operations.DeleteOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class DeleteOperation<D extends Data> extends Operation<
    DeleteOperationIdl,
    DeleteOperationRequest,
    ReadOperationResponse<D>> {

  protected DeleteOperation(final DeleteOperationIdl declaration) {
    super(declaration);
  }
}
