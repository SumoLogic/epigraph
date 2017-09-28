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
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.NonReadRequestUrl;
import ws.epigraph.url.parser.psi.UrlInputProjection;
import ws.epigraph.url.parser.psi.UrlNonReadUrl;
import ws.epigraph.url.parser.psi.UrlOutputProjection;
import ws.epigraph.url.projections.req.output.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;

import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class NonReadRequestUrlPsiParser {
  private final @NotNull ReqProjectionPsiParser inputProjectionParser;
  private final @NotNull ReqProjectionPsiParser outputProjectionParser;

  protected NonReadRequestUrlPsiParser(
      final @NotNull ReqProjectionPsiParser inputProjectionParser,
      final @NotNull ReqProjectionPsiParser outputProjectionParser) {

    this.inputProjectionParser = inputProjectionParser;
    this.outputProjectionParser = outputProjectionParser;
  }

  public @NotNull NonReadRequestUrl parseRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull OperationDeclaration op,
      @NotNull UrlNonReadUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @Nullable OpFieldProjection opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null) {

      return parseRequestUrl(
          null,
          requestParams,
          op,
          psi,
          typesResolver,
          context
      );

    } else {

      @NotNull ReqFieldProjection reqPath = ReqPathPsiParser.parseFieldPath(
          resourceType,
          opPath,
          psi.getReqFieldPath(),
          typesResolver,
          new ReqPathPsiProcessingContext(context)
      );

      return parseRequestUrl(
          reqPath,
          requestParams,
          op,
          psi,
          typesResolver,
          context
      );

    }
  }

  private @NotNull NonReadRequestUrl parseRequestUrl(
      final @Nullable ReqFieldProjection reqFieldPath,
      final Map<String, GDatum> requestParams,
      final @NotNull OperationDeclaration op,
      final @NotNull UrlNonReadUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {


    @NotNull TypeApi opOutputType = op.outputType();
    TypesResolver outputResolver = addTypeNamespace(opOutputType, typesResolver);

    UrlInputProjection inputProjectionPsi = psi.getInputProjection();
    UrlOutputProjection outputProjectionPsi = psi.getOutputProjection();

    @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection;

    @Nullable TypeApi opInputType = op.inputType();
    @Nullable OpFieldProjection inputProjection = op.inputProjection();

    if (opInputType == null || inputProjection == null) {
      if (inputProjectionPsi != null)
        context.addError("Input projection is not supported by operation", inputProjectionPsi);

      inputStepsAndProjection = null;
    } else {
      TypesResolver inputResolver = addTypeNamespace(opInputType, typesResolver);
      inputStepsAndProjection = inputProjectionPsi == null
                                ? null
                                : RequestUrlPsiParserUtil.parseProjection(
                                    opInputType.dataType(),
                                    inputProjection,
                                    inputProjectionPsi.getReqTrunkFieldProjection(),
                                    inputProjectionPsi.getPlus() != null,
                                    inputProjectionParser,
                                    inputResolver,
                                    context
                                );
    }

    final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseProjection(
            opOutputType.dataType(),
            op.outputProjection(),
            outputProjectionPsi == null ? null : outputProjectionPsi.getReqTrunkFieldProjection(),
            outputProjectionPsi != null && outputProjectionPsi.getPlus() != null,
            outputProjectionParser,
            outputResolver,
            context
        );

    return new NonReadRequestUrl(
        psi.getQid().getCanonicalName(),
        reqFieldPath,
        inputStepsAndProjection,
        outputStepsAndProjection,
        requestParams
    );
  }

}
