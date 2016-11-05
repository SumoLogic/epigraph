package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.service.operations.Operation;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationSearchFailure<O extends Operation<?, ?, ?>> implements OperationSearchResult<O> {
  @NotNull
  private final Map<O, List<PsiProcessingError>> errors;

  public OperationSearchFailure(final @NotNull Map<O, List<PsiProcessingError>> errors) { this.errors = errors; }

  @Contract(pure = true)
  public @NotNull Map<O, List<PsiProcessingError>> errors() { return errors; }
}
