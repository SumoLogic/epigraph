package ws.epigraph.url.projections.req.path;

import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathParsingResult<P> {
  @NotNull
  private final P path;

  // only one of these can be non-null
  @Nullable
  private final UrlReqOutputTrunkVarProjection trunkProjectionPsi;
  @Nullable
  private final UrlReqOutputComaVarProjection comaProjectionPsi;
  @NotNull
  private final List<PsiProcessingError> errors;

  public ReadReqPathParsingResult(
      @NotNull P path,
      @Nullable UrlReqOutputTrunkVarProjection trunkProjectionPsi,
      @Nullable UrlReqOutputComaVarProjection comaProjectionPsi,
      final @NotNull List<PsiProcessingError> errors) {

    this.path = path;
    this.trunkProjectionPsi = trunkProjectionPsi;
    this.comaProjectionPsi = comaProjectionPsi;
    this.errors = errors;
  }

  @NotNull
  public P path() { return path; }

  @Nullable
  public UrlReqOutputTrunkVarProjection trunkProjectionPsi() { return trunkProjectionPsi; }

  @Nullable
  public UrlReqOutputComaVarProjection comaProjectionPsi() { return comaProjectionPsi; }

  @NotNull
  public List<PsiProcessingError> errors() { return errors; }
}
