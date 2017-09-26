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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpProjectionPsiParser;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpPsiProcessingContext;
import ws.epigraph.projections.op.output.OpReferenceContext;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpVarPath;
import ws.epigraph.test.TestUtil;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.input.ReqInputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqReferenceContext;
import ws.epigraph.url.projections.req.output.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionPsiParser;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class ReqTestUtil {
  private ReqTestUtil() {}

  public static @NotNull OpEntityProjection parseOpOutputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpOutputProjectionsPsiParser.INSTANCE, varDataType, projectionString, resolver);
  }

  public static @NotNull OpEntityProjection parseOpInputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpInputProjectionsPsiParser.INSTANCE, varDataType, projectionString, resolver);
  }

  public static @NotNull OpEntityProjection parseOpDeleteVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpDeleteProjectionsPsiParser.INSTANCE, varDataType, projectionString, resolver);
  }

  private static @NotNull OpEntityProjection parseOpEntityProjection(
      @NotNull OpProjectionPsiParser parser,
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(true, context -> {
      OpReferenceContext opOutputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpEntityProjection vp = parser.parseVarProjection(
          varDataType,
          false,
          psiVarProjection,
          resolver,
          opPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();

      return vp;
    });

  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqOutputVarProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqVarProjection(
        ReqOutputProjectionPsiParser.INSTANCE,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqInputVarProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqVarProjection(
        ReqInputProjectionPsiParser.INSTANCE,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqUpdateVarProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqVarProjection(
        ReqUpdateProjectionPsiParser.INSTANCE,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqDeleteVarProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqVarProjection(
        ReqDeleteProjectionPsiParser.INSTANCE,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqVarProjection(
      @NotNull ReqProjectionPsiParser parser,
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(true, context -> {
      ReqReferenceContext reqOutputReferenceContext =
          new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
          new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);

      @NotNull StepsAndProjection<ReqEntityProjection> res =
          parser.parseTrunkVarProjection(
              type,
              false,
              op,
              psi,
              resolver,
              reqOutputPsiProcessingContext
          );

      reqOutputReferenceContext.ensureAllReferencesResolved();

      return res;
    });
  }

  public static @NotNull OpVarPath parseOpVarPath(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    try {
      return parseOpVarPath(varDataType, projectionString, true, resolver);
    } catch (PsiProcessingException e) { // can't happen..
      e.printStackTrace();
      fail(e.getMessage());
    }

    throw new RuntimeException("unreachable");
  }

  public static @NotNull OpVarPath parseOpVarPath(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      boolean catchPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    final TestUtil.PsiParserClosure<OpVarPath> closure = context -> {
      OpReferenceContext referenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext psiProcessingContext =
          new OpPsiProcessingContext(context, referenceContext);
      OpPathPsiProcessingContext opPathPsiProcessingContext =
          new OpPathPsiProcessingContext(context, psiProcessingContext);

      OpVarPath vp = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver, opPathPsiProcessingContext);

      referenceContext.ensureAllReferencesResolved();

      return vp;
    };

    return catchPsiErrors ? runPsiParser(true, closure) : runPsiParserNotCatchingErrors(closure);
  }


}
