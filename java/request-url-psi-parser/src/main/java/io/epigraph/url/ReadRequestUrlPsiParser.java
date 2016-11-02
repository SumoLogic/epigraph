package io.epigraph.url;

import io.epigraph.gdata.GDatum;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.path.OpFieldPath;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqVarPath;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DataType;
import io.epigraph.url.parser.psi.UrlReadUrl;
import io.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import io.epigraph.url.projections.req.ReqParserUtil;
import io.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static io.epigraph.url.projections.req.ReqParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadRequestUrlPsiParser {

  @Nullable
  public static ReadRequestUrl parseReadRequestUrl(
      @NotNull DataType resourceType,
      @NotNull ReadOperationIdl op,
      @NotNull UrlReadUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

//    @Nullable final UrlUrl urlPsi = PsiTreeUtil.getChildOfType(psi, UrlUrl.class);
//    if (urlPsi == null) return null;

    final @NotNull String fieldName = psi.getQid().getCanonicalName();

    final ReqVarPath path;

    final @Nullable OpFieldPath opPath = op.path();
    //todo
    /*if (opPath != null) {
      @NotNull final UrlReqVarPath pathPsi = psi.getReqVarPath();
      path = ReqPathPsiParser.parseVarPath(opPath, resourceType, pathPsi, typesResolver);
    } else*/ path = null;

    DataType pathTipType =
        path == null ? resourceType
                     : ProjectionUtils.tipType(path);

    final @NotNull UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();

    TypesResolver newResolver = ReqParserUtil.addTypeNamespace(pathTipType.type, typesResolver);

    @NotNull final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
        ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
            true, resourceType,
            op.outputProjection(),
            fieldProjectionPsi, newResolver,
            errors
        );

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList());

    int pathSteps = stepsAndProjection.pathSteps();

    return new ReadRequestUrl(
        fieldName,
        path,
        new StepsAndProjection<>(
            pathSteps == 0 ? 0 : pathSteps - 1,
            stepsAndProjection.projection()
        ),
        requestParams
    );
  }


}
