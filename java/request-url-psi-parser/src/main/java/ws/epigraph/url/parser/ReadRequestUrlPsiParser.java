/*
 * Copyright 2016 Sumo Logic
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

import ws.epigraph.gdata.GDatum;
import ws.epigraph.idl.operations.ReadOperationDeclaration;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataType;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.path.ReadReqPathParsingResult;
import ws.epigraph.url.projections.req.path.ReadReqPathPsiParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReadRequestUrlPsiParser {

  private ReadRequestUrlPsiParser() {}

  public static @NotNull ReadRequestUrl parseReadRequestUrl(
      @NotNull DataType resourceType,
      @NotNull ReadOperationDeclaration op,
      @NotNull UrlReadUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), errors);

    if (opPath == null)
      return parseReadRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, errors);
    else
      return parseReadRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, errors);
  }

  private static @NotNull ReadRequestUrl parseReadRequestUrlWithPath(
      final @NotNull DataType resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull ReadOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull ReadReqPathParsingResult<ReqFieldPath> pathParsingResult = ReadReqPathPsiParser.parseFieldPath(
        resourceType,
        opPath,
        psi.getReqOutputTrunkFieldProjection(),
        typesResolver,
        errors
    );

    @NotNull ReqFieldPath reqPath = pathParsingResult.path();
    DataType pathTipType = ProjectionUtils.tipType(reqPath.varProjection());
    TypesResolver newResolver = addTypeNamespace(pathTipType.type, typesResolver);

    final UrlReqOutputTrunkVarProjection trunkVarProjection = pathParsingResult.trunkProjectionPsi();
    final UrlReqOutputComaVarProjection comaVarProjection = pathParsingResult.comaProjectionPsi();

    final int steps;
    final @NotNull ReqOutputVarProjection varProjection;
    final @NotNull TextLocation fieldLocation;

    if (trunkVarProjection != null) {
      @NotNull StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
          pathTipType,
          op.outputProjection().varProjection(),
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
          op.outputProjection().varProjection(),
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

  private static @NotNull ReadRequestUrl parseReadRequestUrlWithoutPath(
      final @NotNull DataType resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull ReadOperationDeclaration op,
      final @NotNull UrlReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @NotNull UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type, typesResolver);

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
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
