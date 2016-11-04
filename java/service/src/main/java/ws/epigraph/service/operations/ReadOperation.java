package ws.epigraph.service.operations;

import ws.epigraph.data.Data;
import ws.epigraph.idl.operations.ReadOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReadOperation<D extends Data> extends Operation<
    ReadOperationIdl,
    ReadOperationRequest,
    ReadOperationResponse<D>> {

  protected ReadOperation(ReadOperationIdl declaration) {
    super(declaration);
  }
}
