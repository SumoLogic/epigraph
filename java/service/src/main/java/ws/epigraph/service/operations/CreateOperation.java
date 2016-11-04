package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.idl.operations.CreateOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class CreateOperation<D extends Data> extends Operation<
    CreateOperationIdl,
    CreateOperationRequest,
    ReadOperationResponse<D>> {

  protected CreateOperation(final CreateOperationIdl declaration) {
    super(declaration);
  }
}
