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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqReferenceContext;
import ws.epigraph.url.projections.req.path.ReqPartialPathParsingResult;
import ws.epigraph.url.projections.req.path.ReqPartialPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;

import java.util.Map;
import java.util.function.Function;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.addTypeNamespace;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseRequestParams;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class RequestUrlPsiParser {
  private final @NotNull ReqProjectionPsiParser firstProjectionParser;
  private final @Nullable ReqProjectionPsiParser outputProjectionParser;

  /**
   * @param firstProjectionParser  parser for the first projection in the URL (will be output projection
   *                               for reads, input projection for create/update/custom, delete projection
   *                               for delete operation)
   * @param outputProjectionParser optional output projection (second projection in the URL) parser
   *                               for non-read operations
   */
  protected RequestUrlPsiParser(
      final @NotNull ReqProjectionPsiParser firstProjectionParser,
      final @Nullable ReqProjectionPsiParser outputProjectionParser) {

    this.firstProjectionParser = firstProjectionParser;
    this.outputProjectionParser = outputProjectionParser;
  }

  public @NotNull RequestUrl parseRequestUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull OperationDeclaration op,
      @NotNull UrlUrl psi,
      @NotNull TypesResolver typesResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (op.kind() != OperationKind.READ && outputProjectionParser == null)
      throw new IllegalArgumentException("output parser must be specified for non-read operation " + op);

    final @Nullable OpFieldProjection opPath = op.path();

    final Map<String, GDatum> requestParams = parseRequestParams(psi.getRequestParamList(), context);

    if (opPath == null)
      return parseRequestUrlWithoutPath(resourceType, requestParams, op, psi, typesResolver, context);
    else
      return parseRequestUrlWithPath(resourceType, requestParams, op, opPath, psi, typesResolver, context);
  }

  private @NotNull RequestUrl parseRequestUrlWithPath(
      final @NotNull DataTypeApi resourceType,
      final @NotNull Map<String, GDatum> requestParams,
      final @NotNull OperationDeclaration op,
      final @NotNull OpFieldProjection opPath,
      final @NotNull UrlUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @NotNull ReqPartialPathParsingResult<ReqFieldProjection> pathParsingResult = ReqPartialPathPsiParser.parseFieldPath(
        resourceType,
        opPath,
        psi.getReqTrunkFieldProjection(),
        typesResolver,
        new ReqPathPsiProcessingContext(context)
    );

    @NotNull ReqFieldProjection reqPath = pathParsingResult.path();
    DataTypeApi pathTipType = ProjectionUtils.tipType(reqPath.entityProjection());
    TypesResolver pathTipResolver = addTypeNamespace(pathTipType.type(), typesResolver);

    final UrlReqTrunkEntityProjection trunkEntityProjection = pathParsingResult.trunkProjectionPsi();
    final UrlReqComaEntityProjection comaEntityProjection = pathParsingResult.comaProjectionPsi();

    RequestUrl requestWithoutPath = parseWithoutPath(
        pathTipType,
        requestParams,
        op,
        psi.getQid().getCanonicalName(),
        null,
        trunkEntityProjection,
        comaEntityProjection,
        psi.getPlus() != null,
        psi.getOutputProjection(),
        pathTipResolver,
        context
    );

    return new RequestUrl(
        requestWithoutPath.fieldName(),
        reqPath,
        requestWithoutPath.inputProjection(),
        requestWithoutPath.outputProjection(),
        requestWithoutPath.parameters()
    );

  }

  private @NotNull RequestUrl parseRequestUrlWithoutPath(
      final @NotNull DataTypeApi resourceType,
      final Map<String, GDatum> requestParams,
      final @NotNull OperationDeclaration op,
      final @NotNull UrlUrl psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    return parseWithoutPath(
        resourceType,
        requestParams,
        op,
        psi.getQid().getCanonicalName(),
        psi.getReqTrunkFieldProjection(),
        null,
        null,
        psi.getPlus() != null,
        psi.getOutputProjection(),
        typesResolver,
        context
    );
  }

  private @NotNull RequestUrl parseWithoutPath(
      final @NotNull DataTypeApi type,
      final Map<String, GDatum> requestParams,
      final @NotNull OperationDeclaration op,
      final @NotNull String fieldName,

      // one of these should be non-null
      final @Nullable UrlReqTrunkFieldProjection firstTrunkFieldPsi,
      final @Nullable UrlReqTrunkEntityProjection firstTrunkEntityPsi,
      final @Nullable UrlReqComaEntityProjection firstComaEntityPsi,

      final boolean firstProjectionFlagged,
      final @Nullable UrlOutputProjection outputPsi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull PsiProcessingContext context)
      throws PsiProcessingException {

    ReqReferenceContext firstProjectionReferenceContext =
        new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqPsiProcessingContext firstProjectionProcessingContext =
        new ReqPsiProcessingContext(context, firstProjectionReferenceContext);

    if (op.kind() == OperationKind.READ) {

      StepsAndProjection<ReqFieldProjection> stepsAndProjection = parseFirstFieldProjection(
          type,
          firstProjectionFlagged,
          op.outputProjection(),
          firstTrunkFieldPsi,
          firstTrunkEntityPsi,
          firstComaEntityPsi,
          addTypeNamespace(type.type(), typesResolver),
          firstProjectionProcessingContext
      );

      if (stepsAndProjection == null)
        throw new PsiProcessingException("Output projection must be specified", TextLocation.UNKNOWN, context);

      firstProjectionReferenceContext.ensureAllReferencesResolved();

      if (outputPsi != null)
        context.addWarning(
            "Second output projection should not be specified for a read operation, ignoring",
            outputPsi
        );

      return new RequestUrl(
          fieldName,
          null,
          null,
          stepsAndProjection.unwrap(Function.identity()),
          requestParams
      );

    } else {

      TypeApi inputType = op.inputType();
      OpFieldProjection opInputProjection = op.inputProjection();
      assert opInputProjection != null && inputType != null;

      @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection = parseFirstFieldProjection(
          inputType.dataType(),
          firstProjectionFlagged,
          opInputProjection,
          firstTrunkFieldPsi,
          firstTrunkEntityPsi,
          firstComaEntityPsi,
          addTypeNamespace(inputType, typesResolver),
          firstProjectionProcessingContext
      );

      firstProjectionReferenceContext.ensureAllReferencesResolved();

      assert outputProjectionParser != null;

      final StepsAndProjection<ReqFieldProjection> outputStepsAndProjection;
      if (outputPsi == null) {
        outputStepsAndProjection = new StepsAndProjection<>(
            0,
            new ReqFieldProjection(
                outputProjectionParser.createDefaultEntityProjection(
                    op.outputType().dataType(),
                    op.outputProjection().entityProjection(),
                    false, // todo this should come from op
                    TextLocation.UNKNOWN,
                    firstProjectionProcessingContext
                ),
                TextLocation.UNKNOWN
            )
        );
      } else {

        ReqReferenceContext outputProjectionReferenceContext =
            new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqPsiProcessingContext outputProjectionProcessingContext =
            new ReqPsiProcessingContext(context, firstProjectionReferenceContext);

        UrlReqTrunkFieldProjection outputFieldProjectionPsi = outputPsi.getReqTrunkFieldProjection();
        if (outputFieldProjectionPsi == null)
          throw new PsiProcessingException(
              "Incomplete output projection",
              outputPsi,
              context
          );

        outputStepsAndProjection = outputProjectionParser.parseTrunkFieldProjection(
            op.outputType().dataType(),
            outputPsi.getPlus() != null,
            op.outputProjection(),
            outputFieldProjectionPsi,
            addTypeNamespace(op.outputType(), typesResolver),
            outputProjectionProcessingContext
        );

        outputProjectionReferenceContext.ensureAllReferencesResolved();

      }

      return new RequestUrl(
          fieldName,
          null,
          StepsAndProjection.unwrapNullable(inputStepsAndProjection, Function.identity()), // decrease path steps by 1
          outputStepsAndProjection.unwrap(Function.identity()),
          requestParams
      );

    }

  }

  private @Nullable StepsAndProjection<ReqFieldProjection> parseFirstFieldProjection(
      @NotNull DataTypeApi dataType,
      boolean flagged,
      @NotNull OpFieldProjection op,

      // one of these should be non-null
      @Nullable UrlReqTrunkFieldProjection trunkFieldPsi,
      @Nullable UrlReqTrunkEntityProjection trunkEntityPsi,
      @Nullable UrlReqComaEntityProjection comaEntityPsi,

      @NotNull TypesResolver resolver,
      @NotNull ReqPsiProcessingContext context

  ) throws PsiProcessingException {

    if (trunkFieldPsi != null && !trunkFieldPsi.getText().isEmpty())
      return firstProjectionParser.parseTrunkFieldProjection(
          dataType,
          flagged,
          op,
          trunkFieldPsi,
          resolver,
          context
      );

    else if (trunkEntityPsi != null && !trunkEntityPsi.getText().isEmpty())
      return firstProjectionParser.parseTrunkEntityProjection(
          dataType,
          flagged,
          op.entityProjection(),
          trunkEntityPsi,
          resolver,
          context
      ).wrap(projection -> new ReqFieldProjection(projection, projection.location()));

    else if (comaEntityPsi != null && !comaEntityPsi.getText().isEmpty())
      return firstProjectionParser.parseComaEntityProjection(
          dataType,
          flagged,
          op.entityProjection(),
          comaEntityPsi,
          resolver,
          context
      ).wrap(projection -> new ReqFieldProjection(projection, projection.location()));

    return null;
  }

}
