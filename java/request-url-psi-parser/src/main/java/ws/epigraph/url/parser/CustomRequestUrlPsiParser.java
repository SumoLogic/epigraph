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
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.CustomOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.CustomRequestUrl;
import ws.epigraph.url.parser.psi.UrlCustomUrl;
import ws.epigraph.url.parser.psi.UrlInputProjection;
import ws.epigraph.url.parser.psi.UrlOutputProjection;
import ws.epigraph.url.projections.req.input.ReqInputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;

import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class CustomRequestUrlPsiParser {

  private CustomRequestUrlPsiParser() {}

  public static @NotNull CustomRequestUrl parseCustomRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull CustomOperationDeclaration op,
      @NotNull UrlCustomUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null) {

      return parseCustomRequestUrl(
          null,
          requestParams,
          op,
          psi,
          typesResolver,
          context
      );

    } else {

      final @NotNull ReqFieldPath reqPath =
          ReqPathPsiParser.parseFieldPath(
              resourceType,
              opPath,
              psi.getReqFieldPath(),
              typesResolver,
              new ReqPathPsiProcessingContext(context)
          );

      return parseCustomRequestUrl(
          reqPath,
          requestParams,
          op,
          psi,
          typesResolver,
          context
      );

    }
  }

  private static @NotNull CustomRequestUrl parseCustomRequestUrl(
      final @Nullable ReqFieldPath reqFieldPath,
      final Map<String, GDatum> requestParams,
      final @NotNull CustomOperationDeclaration op,
      final @NotNull UrlCustomUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    // already calculated based on outputType/path declared in idl
    @Nullable TypeApi opInputType = op.inputType();
    TypesResolver inputResolver = opInputType == null ? typesResolver : addTypeNamespace(opInputType, typesResolver);

    @NotNull TypeApi opOutputType = op.outputType();
    TypesResolver outputResolver = addTypeNamespace(opOutputType, typesResolver);

    UrlInputProjection inputProjectionPsi = psi.getInputProjection();
    UrlOutputProjection outputProjectionPsi = psi.getOutputProjection();

    OpOutputFieldProjection opInputProjection = op.inputProjection();

    assert opInputProjection == null || opInputType != null;

    final @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection =
        opInputProjection == null || inputProjectionPsi == null ? null :
        RequestUrlPsiParserUtil.parseProjection(
            opInputType.dataType(),
            opInputProjection,
            inputProjectionPsi.getReqOutputTrunkFieldProjection(),
            ReqInputProjectionPsiParser.INSTANCE,
            inputResolver,
            context
        );

    if (opInputProjection == null && inputProjectionPsi != null)
      context.addError("Input projection is not supported by the operation", inputProjectionPsi);

    final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseProjection(
            opOutputType.dataType(),
            op.outputProjection(),
            outputProjectionPsi == null ? null : outputProjectionPsi.getReqOutputTrunkFieldProjection(),
            ReqOutputProjectionPsiParser.INSTANCE,
            outputResolver,
            context
        );

    return new CustomRequestUrl(
        psi.getQid().getCanonicalName(),
        reqFieldPath,
        inputStepsAndProjection,
        outputStepsAndProjection,
        requestParams
    );
  }

}
