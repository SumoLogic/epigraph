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
import ws.epigraph.idl.operations.CustomOperationIdl;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;
import ws.epigraph.url.CustomRequestUrl;
import ws.epigraph.url.parser.psi.UrlCustomUrl;
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
public class CustomRequestUrlParser {

  @NotNull
  public static CustomRequestUrl parseCustomRequestUrl(
      @NotNull DataType resourceType,
      @NotNull CustomOperationIdl op,
      @NotNull UrlCustomUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), errors);

    if (opPath != null)
      return parseCustomRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, errors);
    else
      return parseCustomRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, errors);
  }

  @NotNull
  private static CustomRequestUrl parseCustomRequestUrlWithPath(
      final @NotNull DataType resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull CustomOperationIdl op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlCustomUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull final ReqFieldPath reqPath =
        ReqPathPsiParser.parseFieldPath(resourceType, opPath, psi.getReqFieldPath(), typesResolver, errors);

    final @NotNull Type opOutputType = op.outputType(); // already calculated based on outputType/path declared in idl

    TypesResolver newResolver = addTypeNamespace(opOutputType, typesResolver);
    @NotNull DataType outputDataType = new DataType(opOutputType, null);

    @NotNull final StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            outputDataType,
            op.outputProjection(),
            psi.getReqOutputTrunkFieldProjection(),
            newResolver,
            errors
        );

    return new CustomRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        getInputProjection(op, psi, typesResolver, errors),
        outputStepsAndProjection,
        requestParams
    );

  }

  @NotNull
  private static CustomRequestUrl parseCustomRequestUrlWithoutPath(
      final @NotNull DataType resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull CustomOperationIdl op,
      final @NotNull UrlCustomUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @Nullable UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type, typesResolver);

    final StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            resourceType,
            op.outputProjection(),
            fieldProjectionPsi,
            newResolver,
            errors
        );

    return new CustomRequestUrl(
        psi.getQid().getCanonicalName(),
        null,
        getInputProjection(op, psi, typesResolver, errors),
        outputStepsAndProjection,
        requestParams
    );
  }

  @Nullable
  private static ReqInputFieldProjection getInputProjection(
      final @NotNull CustomOperationIdl op,
      final @NotNull UrlCustomUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    final ReqInputFieldProjection inputProjection;

    final @Nullable UrlReqInputFieldProjection inputProjectionPsi = psi.getReqInputFieldProjection();
    final @Nullable OpInputFieldProjection opInputProjection = op.inputProjection();
    if (inputProjectionPsi == null) inputProjection = null;
    else {
      final @Nullable Type opInputType = op.inputType();
      assert opInputType != null;

      if (opInputProjection == null)
        throw new PsiProcessingException(
            "Input projection is not supported by the operation",
            inputProjectionPsi,
            errors
        );

      @NotNull DataType inputDataType = new DataType(opInputType, null);
      inputProjection = ReqInputProjectionsPsiParser.parseFieldProjection(
          inputDataType,
          opInputProjection,
          inputProjectionPsi,
          typesResolver,
          errors
      );
    }
    return inputProjection;
  }

}
