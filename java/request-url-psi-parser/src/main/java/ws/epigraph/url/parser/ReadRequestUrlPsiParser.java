/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.url.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqReferenceContext;
import ws.epigraph.url.projections.req.path.ReadReqPathParsingResult;
import ws.epigraph.url.projections.req.path.ReadReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;

import java.util.Collections;
import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReadRequestUrlPsiParser {

  private ReadRequestUrlPsiParser() {}

  public static @NotNull ReadRequestUrl parseReadRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull ReadOperationDeclaration op,
      @NotNull UrlReadUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null)
      return parseReadRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, context);
    else
      return parseReadRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, context);
  }

  private static @NotNull ReadRequestUrl parseReadRequestUrlWithPath(
      final @NotNull DataTypeApi resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull ReadOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @NotNull ReadReqPathParsingResult<ReqFieldPath> pathParsingResult = ReadReqPathPsiParser.parseFieldPath(
        resourceType,
        opPath,
        psi.getReqTrunkFieldProjection(),
        typesResolver,
        new ReqPathPsiProcessingContext(context)
    );

    @NotNull ReqFieldPath reqPath = pathParsingResult.path();
    DataTypeApi pathTipType = ProjectionUtils.tipType(reqPath.varProjection());
    TypesResolver newResolver = addTypeNamespace(pathTipType.type(), typesResolver);

    final UrlReqTrunkEntityProjection trunkEntityProjection = pathParsingResult.trunkProjectionPsi();
    final UrlReqComaEntityProjection comaEntityProjection = pathParsingResult.comaProjectionPsi();

    final int steps;
    final @NotNull ReqEntityProjection varProjection;
    final @NotNull TextLocation fieldLocation;

    ReqReferenceContext reqOutputReferenceContext =
        new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqPsiProcessingContext(context, reqOutputReferenceContext);

    ReqOutputProjectionPsiParser psiParser = ReqOutputProjectionPsiParser.INSTANCE;

    if (trunkEntityProjection != null) {
      @NotNull StepsAndProjection<ReqEntityProjection> r = psiParser.parseTrunkEntityProjection(
          pathTipType,
          false,
          op.outputProjection().varProjection(),
          trunkEntityProjection,
          newResolver,
          reqOutputPsiProcessingContext
      );

      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(trunkEntityProjection);
    } else if (comaEntityProjection != null) {
      StepsAndProjection<ReqEntityProjection> r = psiParser.parseComaEntityProjection(
          pathTipType,
          false,
          op.outputProjection().varProjection(),
          comaEntityProjection,
          newResolver,
          reqOutputPsiProcessingContext
      );
      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(comaEntityProjection);
    } else {
      steps = 0;
      varProjection = new ReqEntityProjection(
          pathTipType.type(),
          false,
          Collections.emptyMap(),
          false,
          null,
          TextLocation.UNKNOWN
      );
      fieldLocation = TextLocation.UNKNOWN;
    }
    reqOutputReferenceContext.ensureAllReferencesResolved();

    return new ReadRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        new StepsAndProjection<>(
            steps,
            new ReqFieldProjection(
//                ReqParams.EMPTY,
//                Annotations.EMPTY,
                varProjection,
//                true,
                fieldLocation
            )
        ),
        requestParams
    );
  }

  private static @NotNull ReadRequestUrl parseReadRequestUrlWithoutPath(
      final @NotNull DataTypeApi resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull ReadOperationDeclaration op,
      final @NotNull UrlReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    final @NotNull UrlReqTrunkFieldProjection fieldProjectionPsi = psi.getReqTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type(), typesResolver);

    ReqReferenceContext reqOutputReferenceContext =
        new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqPsiProcessingContext(context, reqOutputReferenceContext);

    final @NotNull StepsAndProjection<ReqFieldProjection> stepsAndProjection =
        ReqOutputProjectionPsiParser.INSTANCE.parseTrunkFieldProjection(
            resourceType, //op.outputType() ? same for reads
            false,  // ?
            op.outputProjection(),
            fieldProjectionPsi, newResolver,
            reqOutputPsiProcessingContext
        );

    reqOutputReferenceContext.ensureAllReferencesResolved();

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
