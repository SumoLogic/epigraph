package io.epigraph.service.operations;

import io.epigraph.data.Data;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationResponse<D extends Data> extends OperationResponse {
  public ReadOperationResponse(@Nullable D data) {
    super(data);
  }
}
