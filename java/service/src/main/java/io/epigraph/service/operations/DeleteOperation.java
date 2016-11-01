package io.epigraph.service.operations;

import io.epigraph.data.Data;
import io.epigraph.idl.operations.DeleteOperationIdl;

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
