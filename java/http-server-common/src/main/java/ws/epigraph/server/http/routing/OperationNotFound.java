package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.Contract;
import ws.epigraph.service.operations.Operation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationNotFound<O extends Operation<?, ?, ?>> implements OperationSearchResult<O> {
  private static final OperationNotFound<?> INSTANCE = new OperationNotFound();

  private OperationNotFound() {}

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  public static <O extends Operation<?, ?, ?>> OperationNotFound<O> instance() {
    return (OperationNotFound<O>) INSTANCE;
  }
}
