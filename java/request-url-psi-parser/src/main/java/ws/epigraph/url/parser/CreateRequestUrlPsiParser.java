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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.CreateRequestUrl;
import ws.epigraph.url.parser.psi.UrlCreateUrl;
import ws.epigraph.url.parser.psi.UrlReqInputFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.projections.req.input.ReqInputProjectionsPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;

import java.util.List;
import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class CreateRequestUrlPsiParser {

  private CreateRequestUrlPsiParser() {}

  public static @NotNull CreateRequestUrl parseCreateRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull CreateOperationDeclaration op,
      @NotNull UrlCreateUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), errors);

    if (opPath == null)
      return parseCreateRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, errors);
    else
      return parseCreateRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, errors);
  }

  private static @NotNull CreateRequestUrl parseCreateRequestUrlWithPath(
      final @NotNull DataTypeApi resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull CreateOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlCreateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull ReqFieldPath reqPath =
        ReqPathPsiParser.parseFieldPath(resourceType, opPath, psi.getReqFieldPath(), typesResolver, errors);

    final @NotNull TypeApi opOutputType = op.outputType(); // already calculated based on outputType/path declared in idl
    TypesResolver newResolver = addTypeNamespace(opOutputType, typesResolver);

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            opOutputType.dataType(),
            op.outputProjection(),
            psi.getReqOutputTrunkFieldProjection(),
            newResolver,
            errors
        );

    return new CreateRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        getInputProjection(op, psi, typesResolver, errors),
        outputStepsAndProjection,
        requestParams
    );

  }

  private static @Nullable ReqInputFieldProjection getInputProjection(
      final @NotNull CreateOperationDeclaration op,
      final @NotNull UrlCreateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    final ReqInputFieldProjection inputProjection;
    final @Nullable UrlReqInputFieldProjection inputProjectionPsi = psi.getReqInputFieldProjection();
    if (inputProjectionPsi == null) inputProjection = null;
    else {
      final @Nullable OpInputFieldProjection opInputProjection = op.inputProjection();

      final TypeApi inputType = op.inputType();

      inputProjection = ReqInputProjectionsPsiParser.parseFieldProjection(
          inputType.dataType(),
          opInputProjection,
          inputProjectionPsi,
          typesResolver,
          errors
      );
    }
    return inputProjection;
  }

  private static @NotNull CreateRequestUrl parseCreateRequestUrlWithoutPath(
      final @NotNull DataTypeApi resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull CreateOperationDeclaration op,
      final @NotNull UrlCreateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @Nullable UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type(), typesResolver);

    final StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            op.outputType().dataType(),
            op.outputProjection(),
            fieldProjectionPsi,
            newResolver,
            errors
        );

    return new CreateRequestUrl(
        psi.getQid().getCanonicalName(),
        null,
        getInputProjection(op, psi, typesResolver, errors),
        outputStepsAndProjection,
        requestParams
    );
  }

}
