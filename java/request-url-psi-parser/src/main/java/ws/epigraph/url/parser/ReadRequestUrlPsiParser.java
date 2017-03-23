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
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputReferenceContext;
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
        psi.getReqOutputTrunkFieldProjection(),
        typesResolver,
        new ReqPathPsiProcessingContext(context)
    );

    @NotNull ReqFieldPath reqPath = pathParsingResult.path();
    DataTypeApi pathTipType = ProjectionUtils.tipType(reqPath.varProjection());
    TypesResolver newResolver = addTypeNamespace(pathTipType.type(), typesResolver);

    final UrlReqOutputTrunkVarProjection trunkVarProjection = pathParsingResult.trunkProjectionPsi();
    final UrlReqOutputComaVarProjection comaVarProjection = pathParsingResult.comaProjectionPsi();

    final int steps;
    final @NotNull ReqOutputVarProjection varProjection;
    final @NotNull TextLocation fieldLocation;

    ReqOutputReferenceContext reqOutputVarReferenceContext = new ReqOutputReferenceContext(Qn.EMPTY, null);
    ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqOutputPsiProcessingContext(context, reqOutputVarReferenceContext);

    if (trunkVarProjection != null) {
      @NotNull StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
          pathTipType,
          op.outputProjection().varProjection(),
          false,
          trunkVarProjection,
          newResolver,
          reqOutputPsiProcessingContext
      );

      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(trunkVarProjection);
    } else if (comaVarProjection != null) {
      StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseComaVarProjection(
          pathTipType,
          op.outputProjection().varProjection(),
          false,
          comaVarProjection,
          newResolver,
          reqOutputPsiProcessingContext
      );
      steps = r.pathSteps();
      varProjection = r.projection();
      fieldLocation = EpigraphPsiUtil.getLocation(comaVarProjection);
    } else {
      steps = 0;
      varProjection = new ReqOutputVarProjection(
          pathTipType.type(),
          Collections.emptyMap(),
          null,
          false,
          TextLocation.UNKNOWN
      );
      fieldLocation = TextLocation.UNKNOWN;
    }
    reqOutputVarReferenceContext.ensureAllReferencesResolved(context);

    return new ReadRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        new StepsAndProjection<>(
            steps,
            new ReqOutputFieldProjection(
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

    final @NotNull UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type(), typesResolver);

    ReqOutputReferenceContext reqOutputVarReferenceContext = new ReqOutputReferenceContext(Qn.EMPTY, null);
    ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqOutputPsiProcessingContext(context, reqOutputVarReferenceContext);

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
        ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
            false,  // ?
            resourceType,
            op.outputProjection(),
            fieldProjectionPsi, newResolver,
            reqOutputPsiProcessingContext
        );

    reqOutputVarReferenceContext.ensureAllReferencesResolved(context);

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
