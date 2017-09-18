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
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.update.ReqUpdateFieldProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.UpdateRequestUrl;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionsPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdatePsiProcessingContext;
import ws.epigraph.url.projections.req.update.ReqUpdateReferenceContext;

import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UpdateRequestUrlPsiParser {

  private UpdateRequestUrlPsiParser() {}

  public static @NotNull UpdateRequestUrl parseUpdateRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull UpdateOperationDeclaration op,
      @NotNull UrlUpdateUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null)
      return parseUpdateRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, context);
    else
      return parseUpdateRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, context);
  }

  private static @NotNull UpdateRequestUrl parseUpdateRequestUrlWithPath(
      final @NotNull DataTypeApi resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull UpdateOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlUpdateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @NotNull ReqFieldPath reqPath =
        ReqPathPsiParser.parseFieldPath(
            resourceType,
            opPath,
            psi.getReqFieldPath(),
            typesResolver,
            new ReqPathPsiProcessingContext(context)
        );

    final @NotNull TypeApi opOutputType =
        op.outputType(); // already calculated based on outputType/path declared in idl
    final @NotNull TypeApi opInputType = op.inputType();

    TypesResolver newResolver = addTypeNamespace(opOutputType, typesResolver);
    @NotNull DataTypeApi inputDataType = opInputType.dataType();

    final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseProjection(
            opOutputType.dataType(),
            op.outputProjection(),
            psi.getReqOutputTrunkFieldProjection(),
            ReqOutputProjectionPsiParser.INSTANCE,
            newResolver,
            context
        );

    final @Nullable UrlReqUpdateFieldProjection updateProjectionPsi = psi.getReqUpdateFieldProjection();

    ReqUpdateReferenceContext reqUpdateReferenceContext =
        new ReqUpdateReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqUpdatePsiProcessingContext reqUpdatePsiProcessingContext =
        new ReqUpdatePsiProcessingContext(context, reqUpdateReferenceContext);

    final ReqUpdateFieldProjection updateProjection =
        updateProjectionPsi == null ? null : ReqUpdateProjectionsPsiParser.parseFieldProjection(
            inputDataType,
            psi.getPlus() != null,
            op.inputProjection(),
            updateProjectionPsi,
            typesResolver,
            reqUpdatePsiProcessingContext
        );

    reqUpdateReferenceContext.ensureAllReferencesResolved();

    return new UpdateRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        updateProjection,
        outputStepsAndProjection,
        requestParams
    );

  }

  private static @NotNull UpdateRequestUrl parseUpdateRequestUrlWithoutPath(
      final @NotNull DataTypeApi resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull UpdateOperationDeclaration op,
      final @NotNull UrlUpdateUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    final @Nullable UrlReqUpdateFieldProjection updateProjectionPsi = psi.getReqUpdateFieldProjection();

    ReqUpdateReferenceContext reqUpdateReferenceContext =
        new ReqUpdateReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqUpdatePsiProcessingContext reqUpdatePsiProcessingContext =
        new ReqUpdatePsiProcessingContext(context, reqUpdateReferenceContext);

    final ReqUpdateFieldProjection updateProjection =
        updateProjectionPsi == null ? null : ReqUpdateProjectionsPsiParser.parseFieldProjection(
            op.inputType().dataType(),
            psi.getPlus() != null,
            op.inputProjection(),
            updateProjectionPsi,
            typesResolver,
            reqUpdatePsiProcessingContext
        );

    reqUpdateReferenceContext.ensureAllReferencesResolved();

    TypesResolver outputResolver = addTypeNamespace(resourceType.type(), typesResolver);
    final @Nullable UrlReqOutputTrunkFieldProjection outputProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    final StepsAndProjection<ReqFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseProjection(
            op.outputType().dataType(),
            op.outputProjection(),
            outputProjectionPsi,
            ReqOutputProjectionPsiParser.INSTANCE,
            outputResolver,
            context
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
