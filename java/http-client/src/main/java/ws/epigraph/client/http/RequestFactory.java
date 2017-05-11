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

package ws.epigraph.client.http;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.*;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
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
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RequestFactory {
  private RequestFactory() {}

  /**
   * Constructs read request from a string
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param requestString        request projection string
   * @param typesResolver        types resolver
   *
   * @return read request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull ReadOperationRequest constructReadRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkFieldProjection psi =
        EpigraphPsiUtil.parseText(
            requestString,
            UrlSubParserDefinitions.REQ_OUTPUT_FIELD_PROJECTION,
            errorsAccumulator
        );

    String errorsDump = dumpErrors(psi, errorsAccumulator);
    if (errorsDump != null)
      throw new IllegalArgumentException(errorsDump);

    final ReqFieldPath reqPath;
    final ReqOutputFieldProjection reqFieldProjection;

    PsiProcessingContext context = new DefaultPsiProcessingContext();

    OpFieldPath opPath = operationDeclaration.path();

    try {
      if (opPath == null) {
        reqPath = null;
        ReqOutputReferenceContext reqOutputReferenceContext =
            new ReqOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);
        StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection =
            ReqOutputProjectionsPsiParser.parseTrunkFieldProjection(
                false,  // ?
                resourceType,
                operationDeclaration.outputProjection(),
                psi,
                typesResolver,
                reqOutputPsiProcessingContext
            );

        reqOutputReferenceContext.ensureAllReferencesResolved();

        reqFieldProjection = stepsAndProjection.projection();
      } else {
        ReadReqPathParsingResult<ReqFieldPath> pathParsingResult = ReadReqPathPsiParser.parseFieldPath(
            resourceType,
            opPath,
            psi,
            typesResolver,
            new ReqPathPsiProcessingContext(context)
        );

        reqPath = pathParsingResult.path();
        DataTypeApi pathTipType = ProjectionUtils.tipType(reqPath.varProjection());

        UrlReqOutputTrunkVarProjection trunkVarProjection = pathParsingResult.trunkProjectionPsi();
        UrlReqOutputComaVarProjection comaVarProjection = pathParsingResult.comaProjectionPsi();

        ReqOutputReferenceContext reqOutputReferenceContext =
            new ReqOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);

        if (trunkVarProjection != null) {
          StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
              pathTipType,
              operationDeclaration.outputProjection().varProjection(),
              false,
              trunkVarProjection,
              typesResolver,
              reqOutputPsiProcessingContext
          );

          reqFieldProjection = new ReqOutputFieldProjection(r.projection(), r.projection().location());
        } else if (comaVarProjection != null) {
          StepsAndProjection<ReqOutputVarProjection> r = ReqOutputProjectionsPsiParser.parseComaVarProjection(
              pathTipType,
              operationDeclaration.outputProjection().varProjection(),
              false,
              comaVarProjection,
              typesResolver,
              reqOutputPsiProcessingContext
          );
          reqFieldProjection = new ReqOutputFieldProjection(r.projection(), r.projection().location());
        } else {
          ReqOutputVarProjection vp = new ReqOutputVarProjection(
              pathTipType.type(),
              Collections.emptyMap(),
              false, null,
              TextLocation.UNKNOWN
          );
          reqFieldProjection = new ReqOutputFieldProjection(vp, vp.location());
        }

        reqOutputReferenceContext.ensureAllReferencesResolved();
      }

      return new ReadOperationRequest(
          reqPath,
          reqFieldProjection
      );
    } catch (PsiProcessingException e) {
      context.setErrors(e.errors());
    }

    errorsDump = dumpErrors(context.errors());
    throw new IllegalArgumentException(errorsDump);
  }

  private static @Nullable String dumpErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) {

    String errorsDump = dumpErrors(psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()));

    if (errorsDump == null)
      return null;

    String psiDump = DebugUtil.psiToString(psi, true, false).trim();
    return errorsDump + "\nPSI Dump:\n\n" + psiDump;
  }

  public static @Nullable String dumpErrors(final List<PsiProcessingError> errors) {
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (final PsiProcessingError error : errors)
        sb.append(error.location()).append(": ").append(error.message()).append("\n");

      return sb.toString();
    }

    return null;
  }

  static @NotNull List<PsiProcessingError> psiErrorsToPsiProcessingErrors(@NotNull List<PsiErrorElement> errors) {
    return errors.stream().map(e -> new PsiProcessingError(e.getErrorDescription(), e)).collect(Collectors.toList());
  }
}
