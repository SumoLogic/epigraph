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
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.DeleteRequestUrl;
import ws.epigraph.url.parser.psi.UrlDeleteUrl;
import ws.epigraph.url.parser.psi.UrlReqDeleteFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionsPsiParser;
import ws.epigraph.url.projections.req.delete.ReqDeletePsiProcessingContext;
import ws.epigraph.url.projections.req.delete.ReqDeleteReferenceContext;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;

import java.util.Map;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DeleteRequestUrlPsiParser {

  private DeleteRequestUrlPsiParser() {}

  public static @NotNull DeleteRequestUrl parseDeleteRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull DeleteOperationDeclaration op,
      @NotNull UrlDeleteUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final @Nullable OpFieldPath opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null)
      return parseDeleteRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, context);
    else
      return parseDeleteRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, context);
  }

  private static @NotNull DeleteRequestUrl parseDeleteRequestUrlWithPath(
      final @NotNull DataTypeApi resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull DeleteOperationDeclaration op,
      final @NotNull OpFieldPath opPath,
      final @NotNull UrlDeleteUrl psi,
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

    TypesResolver newResolver = addTypeNamespace(opOutputType, typesResolver);
    @NotNull DataTypeApi deleteDataType = op.deleteProjection().varProjection().type().dataType();

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            opOutputType.dataType(),
            op.outputProjection(),
            psi.getReqOutputTrunkFieldProjection(),
            newResolver,
            context
        );

    final @Nullable UrlReqDeleteFieldProjection deleteProjectionPsi = psi.getReqDeleteFieldProjection();

    ReqDeleteReferenceContext reqDeleteVarReferenceContext = new ReqDeleteReferenceContext(Qn.EMPTY, null);
    ReqDeletePsiProcessingContext reqDeletePsiProcessingContext =
        new ReqDeletePsiProcessingContext(context, reqDeleteVarReferenceContext);

    DeleteRequestUrl res = new DeleteRequestUrl(
        psi.getQid().getCanonicalName(),
        reqPath,
        ReqDeleteProjectionsPsiParser.parseFieldProjection(
            deleteDataType,
            op.deleteProjection(),
            deleteProjectionPsi,
            typesResolver,
            reqDeletePsiProcessingContext
        ),
        outputStepsAndProjection,
        requestParams
    );

    reqDeleteVarReferenceContext.ensureAllReferencesResolved(context);

    return res;
  }

  private static @NotNull DeleteRequestUrl parseDeleteRequestUrlWithoutPath(
      final @NotNull DataTypeApi resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull DeleteOperationDeclaration op,
      final @NotNull UrlDeleteUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    final @Nullable UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    TypesResolver newResolver = addTypeNamespace(resourceType.type(), typesResolver);
    final @Nullable UrlReqDeleteFieldProjection deleteProjectionPsi = psi.getReqDeleteFieldProjection();

    final StepsAndProjection<ReqOutputFieldProjection> outputStepsAndProjection =
        RequestUrlPsiParserUtil.parseOutputProjection(
            op.outputType().dataType(),
            op.outputProjection(),
            fieldProjectionPsi,
            newResolver,
            context
        );

    ReqDeleteReferenceContext reqDeleteVarReferenceContext = new ReqDeleteReferenceContext(Qn.EMPTY, null);
    ReqDeletePsiProcessingContext reqDeletePsiProcessingContext =
        new ReqDeletePsiProcessingContext(context, reqDeleteVarReferenceContext);

    DeleteRequestUrl res = new DeleteRequestUrl(
        psi.getQid().getCanonicalName(),
        null,
        ReqDeleteProjectionsPsiParser.parseFieldProjection(
            resourceType,
            op.deleteProjection(),
            deleteProjectionPsi,
            typesResolver,
            reqDeletePsiProcessingContext
        ),
        outputStepsAndProjection,
        requestParams
    );

    reqDeleteVarReferenceContext.ensureAllReferencesResolved(context);

    return res;
  }

}
