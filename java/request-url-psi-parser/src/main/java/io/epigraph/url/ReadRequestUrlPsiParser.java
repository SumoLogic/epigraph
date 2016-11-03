package io.epigraph.url;

import io.epigraph.gdata.GDatum;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.path.OpFieldPath;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import io.epigraph.projections.req.path.ReqFieldPath;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DataType;
import io.epigraph.url.parser.psi.UrlReadUrl;
import io.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import io.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import io.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import io.epigraph.url.projections.req.ReqParserUtil;
import io.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import io.epigraph.url.projections.req.path.ReadReqPathParsingResult;
import io.epigraph.url.projections.req.path.ReadReqPathPsiParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.epigraph.url.projections.req.ReqParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadRequestUrlPsiParser {

  @NotNull
  public static ReadRequestUrl parseReadRequestUrl(
      @NotNull DataType resourceType,
      @NotNull ReadOperationIdl op,
      @NotNull UrlReadUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), errors);

    if (opPath != null)
      return parseReadRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, errors);
    else
      return parseReadRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, errors);
  }

  @NotNull
  private static ReadRequestUrl parseReadRequestUrlWithPath(
      final @NotNull DataType resourceType,
      final Map<String, GDatum> requestParams, final @NotNull ReadOperationIdl op,
      final OpFieldPath opPath,
      final @NotNull UrlReadUrl psi, final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull ReadReqPathParsingResult<ReqFieldPath> pathParsingResult = ReadReqPathPsiParser.parseFieldPath(
        resourceType,
        opPath,
        psi.getReqOutputTrunkFieldProjection(),
        typesResolver,
        errors
    );

    @NotNull ReqFieldPath reqPath = pathParsingResult.path();
    DataType pathTipType = ProjectionUtils.tipType(reqPath.projection());
    TypesResolver newResolver = ReqParserUtil.addTypeNamespace(pathTipType.type, typesResolver);

    final UrlReqOutputTrunkVarProjection trunkVarProjection = pathParsingResult.trunkProjectionPsi();
    final UrlReqOutputComaVarProjection comaVarProjection = pathParsingResult.comaProjectionPsi();

    final int steps;
    @NotNull final ReqOutputVarProjection varProjection;
    @NotNull final TextLocation fieldLocation;

    if (trunkVarProjection != null) {
      @NotNull StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
          pathTipType,
          op.outputProjection().projection(),
          trunkVarProjection,
          newResolver,
          errors
      );
      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(trunkVarProjection);
    } else if (comaVarProjection != null) {
      StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseComaVarProjection(
          pathTipType,
          op.outputProjection().projection(),
          comaVarProjection,
          newResolver,
          errors
      );
      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(comaVarProjection);
    } else {
      steps = 0;
      varProjection = new ReqOutputVarProjection(
          pathTipType.type,
          Collections.emptyMap(),
          null,
          false,
          TextLocation.UNKNOWN
      );
      fieldLocation = TextLocation.UNKNOWN;
    }

    return new ReadRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        new StepsAndProjection<>(
            steps,
            new ReqOutputFieldProjection(
                ReqParams.EMPTY,
                Annotations.EMPTY,
                varProjection,
                true,
                fieldLocation
            )
        ),
        requestParams
    );
  }

  @NotNull
  private static ReadRequestUrl parseReadRequestUrlWithoutPath(
      final @NotNull DataType resourceType,
      final Map<String, GDatum> requestParams, final @NotNull ReadOperationIdl op,
      final @NotNull UrlReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @NotNull UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = ReqParserUtil.addTypeNamespace(resourceType.type, typesResolver);

    @NotNull final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
        ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
            true, resourceType,
            op.outputProjection(),
            fieldProjectionPsi, newResolver,
            errors
        );

    int pathSteps = stepsAndProjection.pathSteps();

    return new ReadRequestUrl(
        psi.getQid().getCanonicalName(),
        null,
        new StepsAndProjection<>(
            pathSteps == 0 ? 0 : pathSteps - 1,
            stepsAndProjection.projection()
        ),
        requestParams
    );
  }

}
