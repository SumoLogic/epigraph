package io.epigraph.service.operations;

import io.epigraph.idl.operations.ReadOperationIdl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReadOperation
    extends Operation<ReadOperationIdl, ReadOperationRequest, ReadOperationResponse> {

  protected ReadOperation(ReadOperationIdl declaration) {
    super(declaration);
  }
}
