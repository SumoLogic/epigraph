package io.epigraph.service.operations;

import io.epigraph.data.Data;
import io.epigraph.idl.operations.UpdateOperationIdl;

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
