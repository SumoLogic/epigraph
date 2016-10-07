package io.epigraph.service.operations;

import io.epigraph.data.Data;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationResponse {
  @Nullable
  private final Data data;

  protected OperationResponse(@Nullable Data data) {this.data = data;}

  @Nullable
  public Data getData() { return data; }
}
