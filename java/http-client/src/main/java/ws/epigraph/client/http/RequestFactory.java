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
import ws.epigraph.data.Data;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.projections.req.path.ReqVarPath;
import ws.epigraph.psi.*;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.*;
import ws.epigraph.service.operations.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputComaVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.parser.psi.UrlReqVarPath;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqReferenceContext;
import ws.epigraph.url.projections.req.path.ReadReqPathParsingResult;
import ws.epigraph.url.projections.req.path.ReadReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionPsiParser;

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

    throwErrors(psi, errorsAccumulator);

    final ReqFieldPath reqPath;
    final ReqFieldProjection reqFieldProjection;

    PsiProcessingContext context = new DefaultPsiProcessingContext();

    OpFieldPath opPath = operationDeclaration.path();

    try {
      if (opPath == null) {
        reqPath = null;
        ReqReferenceContext reqOutputReferenceContext =
            new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);
        StepsAndProjection<ReqFieldProjection> stepsAndProjection =
            ReqOutputProjectionPsiParser.INSTANCE.parseTrunkFieldProjection(
                resourceType,
                false,  // ?
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

        ReqReferenceContext reqOutputReferenceContext =
            new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);

        if (trunkVarProjection != null) {
          StepsAndProjection<ReqEntityProjection> r = ReqOutputProjectionPsiParser.INSTANCE.parseTrunkVarProjection(
              pathTipType,
              false,
              operationDeclaration.outputProjection().varProjection(),
              trunkVarProjection,
              typesResolver,
              reqOutputPsiProcessingContext
          );

          reqFieldProjection = new ReqFieldProjection(r.projection(), r.projection().location());
        } else if (comaVarProjection != null) {
          StepsAndProjection<ReqEntityProjection> r = ReqOutputProjectionPsiParser.INSTANCE.parseComaVarProjection(
              pathTipType,
              false,
              operationDeclaration.outputProjection().varProjection(),
              comaVarProjection,
              typesResolver,
              reqOutputPsiProcessingContext
          );
          reqFieldProjection = new ReqFieldProjection(r.projection(), r.projection().location());
        } else {
          ReqEntityProjection vp = new ReqEntityProjection(
              pathTipType.type(),
              false,
              Collections.emptyMap(),
              false,
              null,
              TextLocation.UNKNOWN
          );
          reqFieldProjection = new ReqFieldProjection(vp, vp.location());
        }

        reqOutputReferenceContext.ensureAllReferencesResolved();
      }

      return new ReadOperationRequest(
          reqPath,
          reqFieldProjection
      );
    } catch (PsiProcessingException e) {
      context.setErrors(e.messages());
    }

    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  /**
   * Constructs create request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param inputRequestString   optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull CreateOperationRequest constructCreateRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull CreateOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String inputRequestString,
      @NotNull Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldPath reqFieldPath = null;

    if (pathString != null) {
      OpFieldPath opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqVarPath reqVarPath = parseReqPath(pathString, resourceType, opPath.varProjection(), typesResolver);
      reqFieldPath = new ReqFieldPath(
          reqVarPath,
          TextLocation.UNKNOWN
      );

    }

    ReqFieldProjection reqOutputFieldProjection = new ReqFieldProjection(
        parseReqOutputProjection(
            outputRequestString,
            operationDeclaration.outputType().dataType(),
            operationDeclaration.outputProjection().varProjection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    ReqFieldProjection reqInputFieldProjection = null;
    if (inputRequestString != null) {
      reqInputFieldProjection = new ReqFieldProjection(
          parseReqOutputProjection(
              inputRequestString,
              operationDeclaration.inputType().dataType(),
              operationDeclaration.inputProjection().varProjection(),
              typesResolver
          ).projection(),
          TextLocation.UNKNOWN
      );
    }

    return new CreateOperationRequest(
        reqFieldPath,
        requestData,
        reqInputFieldProjection,
        reqOutputFieldProjection
    );
  }

  /**
   * Constructs update request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param updateRequestString  optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull UpdateOperationRequest constructUpdateRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull UpdateOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String updateRequestString,
      @NotNull Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldPath reqFieldPath = null;

    if (pathString != null) {
      OpFieldPath opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqVarPath reqVarPath = parseReqPath(pathString, resourceType, opPath.varProjection(), typesResolver);
      reqFieldPath = new ReqFieldPath(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    ReqFieldProjection reqFieldProjection = new ReqFieldProjection(
        parseReqOutputProjection(
            outputRequestString,
            operationDeclaration.outputType().dataType(),
            operationDeclaration.outputProjection().varProjection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    ReqFieldProjection reqUpdateFieldProjection = null;
    if (updateRequestString != null) {
      // parse leading '+'
      boolean replace = updateRequestString.startsWith("+");

      reqUpdateFieldProjection = new ReqFieldProjection(
          parseReqUpdateProjection(
              replace ? updateRequestString.substring(1) : updateRequestString,
              replace,
              operationDeclaration.inputType().dataType(),
              operationDeclaration.inputProjection().varProjection(),
              typesResolver
          ),
          TextLocation.UNKNOWN
      );
    }

    return new UpdateOperationRequest(
        reqFieldPath,
        requestData,
        reqUpdateFieldProjection,
        reqFieldProjection
    );
  }

  /**
   * Constructs delete request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param deleteRequestString  optional (nullable) request projection string
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull DeleteOperationRequest constructDeleteRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull DeleteOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @NotNull String deleteRequestString,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldPath reqFieldPath = null;

    if (pathString != null) {
      OpFieldPath opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqVarPath reqVarPath = parseReqPath(pathString, resourceType, opPath.varProjection(), typesResolver);
      reqFieldPath = new ReqFieldPath(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    ReqFieldProjection reqFieldProjection = new ReqFieldProjection(
        parseReqOutputProjection(
            outputRequestString,
            operationDeclaration.outputType().dataType(),
            operationDeclaration.outputProjection().varProjection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    ReqFieldProjection reqDeleteFieldProjection = new ReqFieldProjection(
        parseReqDeleteProjection(
            deleteRequestString,
            operationDeclaration.deleteProjection().varProjection().type().dataType(),
            operationDeclaration.deleteProjection().varProjection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    return new DeleteOperationRequest(
        reqFieldPath,
        reqDeleteFieldProjection,
        reqFieldProjection
    );
  }

  /**
   * Constructs custom operation request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param inputRequestString   optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull CustomOperationRequest constructCustomRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull CustomOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String inputRequestString,
      @Nullable Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldPath reqFieldPath = null;

    if (pathString != null) {
      OpFieldPath opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqVarPath reqVarPath = parseReqPath(pathString, resourceType, opPath.varProjection(), typesResolver);
      reqFieldPath = new ReqFieldPath(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    ReqFieldProjection reqOutputFieldProjection = new ReqFieldProjection(
        parseReqOutputProjection(
            outputRequestString,
            operationDeclaration.outputType().dataType(),
            operationDeclaration.outputProjection().varProjection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    ReqFieldProjection reqInputFieldProjection = null;
    if (inputRequestString != null) {
      OpFieldProjection opInputFieldProjection = operationDeclaration.inputProjection();
      if (opInputFieldProjection == null)
        throw new IllegalArgumentException(
            String.format(
                "Input projection specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      TypeApi inputType = operationDeclaration.inputType();
      assert inputType != null;

      reqInputFieldProjection = new ReqFieldProjection(
          parseReqOutputProjection(
              inputRequestString,
              inputType.dataType(),
              opInputFieldProjection.varProjection(),
              typesResolver
          ).projection(),
          TextLocation.UNKNOWN
      );
    }

    return new CustomOperationRequest(
        reqFieldPath,
        requestData,
        reqInputFieldProjection,
        reqOutputFieldProjection
    );
  }

  private static @NotNull ReqVarPath parseReqPath(
      @NotNull String path,
      @NotNull DataTypeApi type,
      @NotNull OpVarPath op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    UrlReqVarPath psi = getReqPathPsi(path);

    PsiProcessingContext context = new DefaultPsiProcessingContext();
    try {
      return ReqPathPsiParser.parseVarPath(
          op,
          type,
          psi,
          resolver,
          new ReqPathPsiProcessingContext(context)
      );
    } catch (PsiProcessingException e) {
      context.setErrors(e.messages());
    }

    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  private static @NotNull UrlReqVarPath getReqPathPsi(@NotNull String projectionString)
      throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqVarPath psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_VAR_PATH,
        errorsAccumulator
    );

    throwErrors(psiVarPath, errorsAccumulator);

    return psiVarPath;
  }

  private static @NotNull StepsAndProjection<ReqEntityProjection> parseReqOutputProjection(
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(ReqOutputProjectionPsiParser.INSTANCE, false, projection, type, op, resolver);
  }

  private static @NotNull StepsAndProjection<ReqEntityProjection> parseReqDeleteProjection(
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(ReqDeleteProjectionPsiParser.INSTANCE, false, projection, type, op, resolver);
  }

  private static @NotNull StepsAndProjection<ReqEntityProjection> parseReqProjection(
      @NotNull ReqProjectionPsiParser parser,
      boolean flagged,
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpEntityProjection op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    UrlReqOutputTrunkVarProjection psi = getReqOutputProjectionPsi(projection);

    PsiProcessingContext context = new DefaultPsiProcessingContext();
    ReqReferenceContext referenceContext =
        new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqOutputPsiProcessingContext(context, referenceContext);

    try {
      @NotNull StepsAndProjection<ReqEntityProjection> res =
          parser.parseTrunkVarProjection(
              type,
              flagged,
              op,
              psi,
              resolver,
              reqOutputPsiProcessingContext
          );

      referenceContext.ensureAllReferencesResolved();
      throwErrors(context.messages());

      return res;

    } catch (PsiProcessingException e) {
      context.setErrors(e.messages());
    }


    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  private static @NotNull UrlReqOutputTrunkVarProjection getReqOutputProjectionPsi(@NotNull String projection)
      throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psi = EpigraphPsiUtil.parseText(
        projection,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    throwErrors(psi, errorsAccumulator);

    return psi;
  }

  private static @NotNull ReqEntityProjection parseReqUpdateProjection(
      @NotNull String projection,
      boolean replace,
      @NotNull DataTypeApi dataType,
      @NotNull OpEntityProjection op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(
        ReqUpdateProjectionPsiParser.INSTANCE,
        replace,
        projection,
        dataType,
        op,
        resolver
    ).projection();
  }

  private static void throwErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) throws IllegalArgumentException {

    String errors = dumpErrors(psi, errorsAccumulator);
    if (errors != null)
      throw new IllegalArgumentException(errors);
  }

  private static @Nullable String dumpErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) {

    String errorsDump = dumpErrors(psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()));

    if (errorsDump == null)
      return null;

    String psiDump = DebugUtil.psiToString(psi, true, false).trim();
    return "\n" + psi.getText() + "\n\n" + errorsDump + "\nPSI Dump:\n\n" + psiDump;
  }

  private static void throwErrors(@NotNull List<PsiProcessingMessage> errors) {
    String dump = dumpErrors(errors);
    if (dump != null)
      throw new IllegalArgumentException(dump);
  }

  private static @Nullable String dumpErrors(final List<PsiProcessingMessage> errors) {
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (final PsiProcessingMessage error : errors)
        sb.append(error.location()).append(": ").append(error.message()).append("\n");

      return sb.toString();
    }

    return null;
  }

  private static @NotNull List<PsiProcessingMessage> psiErrorsToPsiProcessingErrors(@NotNull List<PsiErrorElement> errors) {
    return errors.stream()
        .map(e -> new PsiProcessingMessage(PsiProcessingMessage.Level.ERROR, e.getErrorDescription(), e))
        .collect(Collectors.toList());
  }
}
