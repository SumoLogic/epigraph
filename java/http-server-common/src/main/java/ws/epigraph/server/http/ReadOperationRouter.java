package ws.epigraph.server.http;

import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.types.DataType;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.ReadRequestUrlPsiParser;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationRouter {
  @NotNull
  public static ReadOperationSearchResult findReadOperation(
      @Nullable String operationName,
      @NotNull UrlReadUrl urlPsi,
      @NotNull Resource resource,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    final @NotNull DataType resourceFieldType = resource.declaration().fieldType();

    if (operationName != null) {
      @Nullable final ReadOperation<?> operation = resource.namedReadOperation(operationName);
      return matchReadOperation(operation, resourceFieldType, urlPsi, resolver);
    } else {
      final Map<ReadOperation<?>, List<PsiProcessingError>> matchingErrors = new HashMap<>();

      for (final ReadOperation<?> operation : resource.unnamedReadOperations()) {
        @NotNull ReadOperationSearchResult matchingResult =
            matchReadOperation(operation, resourceFieldType, urlPsi, resolver);

        if (matchingResult instanceof ReadOperationSearchSuccess)
          return matchingResult;

        if (matchingResult instanceof ReadOperationSearchFailure) {
          ReadOperationSearchFailure searchFailure = (ReadOperationSearchFailure) matchingResult;
          matchingErrors.put(operation, searchFailure.errors().get(operation));
        }

      }

      if (matchingErrors.isEmpty())
        return ReadOperationNotFound.INSTANCE;
      else
        return new ReadOperationSearchFailure(matchingErrors);
    }
  }

  @NotNull
  private static ReadOperationSearchResult matchReadOperation(
      final @Nullable ReadOperation<?> operation,
      final @NotNull DataType resourceFieldType,
      final @NotNull UrlReadUrl urlPsi,
      final @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (operation == null)
      return ReadOperationNotFound.INSTANCE;
    else {
      List<PsiProcessingError> operationErrors = new ArrayList<>();
      @NotNull final ReadRequestUrl readRequestUrl = ReadRequestUrlPsiParser.parseReadRequestUrl(
          resourceFieldType,
          operation.declaration(),
          urlPsi,
          resolver,
          operationErrors
      );

      if (operationErrors.isEmpty())
        return new ReadOperationSearchSuccess(
            readRequestUrl.parameters(),
            readRequestUrl.path(),
            readRequestUrl.outputProjection()
        );
      else
        return new ReadOperationSearchFailure(
            Collections.singletonMap(operation, operationErrors)
        );

    }
  }

  public interface ReadOperationSearchResult {}

  public static final class ReadOperationNotFound implements ReadOperationSearchResult {
    public static final ReadOperationNotFound INSTANCE = new ReadOperationNotFound();

    private ReadOperationNotFound() {}
  }

  public static final class ReadOperationSearchFailure implements ReadOperationSearchResult {
    @NotNull
    private final Map<ReadOperation<?>, List<PsiProcessingError>> errors;

    public ReadOperationSearchFailure(final @NotNull Map<ReadOperation<?>, List<PsiProcessingError>> errors) {
      this.errors = errors;
    }

    @Contract(pure = true)
    @NotNull
    public Map<ReadOperation<?>, List<PsiProcessingError>> errors() { return errors; }
  }

  public static final class ReadOperationSearchSuccess implements ReadOperationSearchResult {
    @NotNull
    private final Map<String, GDatum> requestParams;
    @Nullable
    private final ReqFieldPath path;
    @NotNull
    private final StepsAndProjection<ReqOutputFieldProjection> projection;

    public ReadOperationSearchSuccess(
        final @NotNull Map<String, GDatum> params,
        final @Nullable ReqFieldPath path,
        final @NotNull StepsAndProjection<ReqOutputFieldProjection> projection) {

      requestParams = params;
      this.path = path;
      this.projection = projection;
    }

    @Contract(pure = true)
    @Nullable
    public ReqFieldPath path() { return path; }

    @Contract(pure = true)
    public @NotNull StepsAndProjection<ReqOutputFieldProjection> projection() { return projection; }
  }
}
