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
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;
import ws.epigraph.url.UpdateRequestUrl;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqUpdateFieldProjection;
import ws.epigraph.url.parser.psi.UrlUpdateUrl;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionsPsiParser;

import java.util.List;
import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UpdateRequestUrlPsiParser {

  private UpdateRequestUrlPsiParser() {}

  public static @NotNull UpdateRequestUrl parseUpdateRequestUrl(
      @NotNull DataType resourceType,
      @NotNull UpdateOperationDeclaration op,
      @NotNull UrlUpdateUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), errors);

    if (opPath == null)
      return parseUpdateRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, errors);
    else
      return parseUpdateRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, errors);
  }

  private static @NotNull UpdateRequestUrl parseUpdateRequestUrlWithPath(
      final @NotNull DataType resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull UpdateOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlUpdateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull ReqFieldPath reqPath =
        ReqPathPsiParser.parseFieldPath(resourceType, opPath, psi.getReqFieldPath(), typesResolver, errors);

    final @NotNull Type opOutputType = op.outputType(); // already calculated based on outputType/path declared in idl
    final @NotNull Type opInputType = op.inputType();

    TypesResolver newResolver = addTypeNamespace(opOutputType, typesResolver);
    @NotNull DataType inputDataType = new DataType(opInputType, null);

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            new DataType(opOutputType, null),
            op.outputProjection(),
            psi.getReqOutputTrunkFieldProjection(),
            newResolver,
            errors
        );

    final @Nullable UrlReqUpdateFieldProjection updateProjectionPsi = psi.getReqUpdateFieldProjection();

    final ReqUpdateFieldProjection updateProjection =
        updateProjectionPsi == null ? null : ReqUpdateProjectionsPsiParser.parseFieldProjection(
            inputDataType,
            psi.getPlus() != null,
            op.inputProjection(),
            updateProjectionPsi,
            typesResolver,
            errors
        );

    return new UpdateRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        updateProjection,
        outputStepsAndProjection,
        requestParams
    );

  }

  private static @NotNull UpdateRequestUrl parseUpdateRequestUrlWithoutPath(
      final @NotNull DataType resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull UpdateOperationDeclaration op,
      final @NotNull UrlUpdateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @Nullable UrlReqUpdateFieldProjection updateProjectionPsi = psi.getReqUpdateFieldProjection();

    final ReqUpdateFieldProjection updateProjection =
        updateProjectionPsi == null ? null : ReqUpdateProjectionsPsiParser.parseFieldProjection(
            new DataType(op.inputType(), null),
            psi.getPlus() != null,
            op.inputProjection(),
            updateProjectionPsi,
            typesResolver,
            errors
        );

    TypesResolver outputResolver = addTypeNamespace(resourceType.type, typesResolver);
    final @Nullable UrlReqOutputTrunkFieldProjection outputProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    final StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            new DataType(op.outputType(), null),
            op.outputProjection(),
            outputProjectionPsi,
            outputResolver,
            errors
        );

    return new UpdateRequestUrl(
        psi.getQid().getCanonicalName(),
        null,
        updateProjection,
        outputStepsAndProjection,
        requestParams
    );
  }

}
