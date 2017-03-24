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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.delete.OpDeletePsiProcessingContext;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.delete.OpDeleteReferenceContext;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputPsiProcessingContext;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.output.OpOutputReferenceContext;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.delete.ReqDeleteVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpDeleteVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpInputVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.schema.parser.psi.SchemaOpVarPath;
import ws.epigraph.test.TestUtil;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqDeleteVarProjection;
import ws.epigraph.url.parser.psi.UrlReqInputVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.parser.psi.UrlReqUpdateVarProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionsPsiParser;
import ws.epigraph.url.projections.req.delete.ReqDeletePsiProcessingContext;
import ws.epigraph.url.projections.req.delete.ReqDeleteReferenceContext;
import ws.epigraph.url.projections.req.input.ReqInputProjectionsPsiParser;
import ws.epigraph.url.projections.req.input.ReqInputPsiProcessingContext;
import ws.epigraph.url.projections.req.input.ReqInputReferenceContext;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputReferenceContext;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionsPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdatePsiProcessingContext;
import ws.epigraph.url.projections.req.update.ReqUpdateReferenceContext;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class ReqTestUtil {
  private ReqTestUtil() {}

  public static @NotNull OpOutputVarProjection parseOpOutputVarProjection(
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

    return runPsiParser(context -> {
      OpInputReferenceContext opInputReferenceContext = new OpInputReferenceContext(Qn.EMPTY, null);
      OpOutputReferenceContext opOutputReferenceContext = new OpOutputReferenceContext(Qn.EMPTY, null);

      OpInputPsiProcessingContext opInputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, opInputReferenceContext);

      OpOutputPsiProcessingContext opOutputPsiProcessingContext = new OpOutputPsiProcessingContext(
          context,
          opInputPsiProcessingContext,
          opOutputReferenceContext
      );
      OpOutputVarProjection vp = OpOutputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          opOutputPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved(context);
      opInputReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });

  }

  public static @NotNull StepsAndProjection<ReqOutputVarProjection> parseReqOutputVarProjection(
      @NotNull DataType type,
      @NotNull OpOutputVarProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(context -> {
      ReqOutputReferenceContext reqOutputReferenceContext = new ReqOutputReferenceContext(Qn.EMPTY, null);

      ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
          new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);

      @NotNull StepsAndProjection<ReqOutputVarProjection> res = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
          type,
          op,
          false,
          psi,
          resolver,
          reqOutputPsiProcessingContext
      );

      reqOutputReferenceContext.ensureAllReferencesResolved(context);

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
      OpInputReferenceContext opInputReferenceContext = new OpInputReferenceContext(Qn.EMPTY, null);

      OpInputPsiProcessingContext opInputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, opInputReferenceContext);
      OpPathPsiProcessingContext opPathPsiProcessingContext =
          new OpPathPsiProcessingContext(context, opInputPsiProcessingContext);

      OpVarPath vp = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver, opPathPsiProcessingContext);

      opInputReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    };

    return catchPsiErrors ? runPsiParser(closure) : runPsiParserNotCatchingErrors(closure);
  }

  public static @NotNull ReqUpdateVarProjection parseReqUpdateVarProjection(
      @NotNull DataType type,
      @NotNull OpInputVarProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqUpdateVarProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_UPDATE_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(context -> {
      ReqUpdateReferenceContext reqUpdateReferenceContext = new ReqUpdateReferenceContext(Qn.EMPTY, null);
      ReqUpdatePsiProcessingContext reqUpdatePsiProcessingContext =
          new ReqUpdatePsiProcessingContext(context, reqUpdateReferenceContext);

      ReqUpdateVarProjection vp = ReqUpdateProjectionsPsiParser.parseVarProjection(
          type,
          op,
          psi,
          resolver,
          reqUpdatePsiProcessingContext
      );

      reqUpdateReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });
  }

  public static @NotNull ReqInputVarProjection parseReqInputVarProjection(
      @NotNull DataType type,
      @NotNull OpInputVarProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqInputVarProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(context -> {
      ReqInputReferenceContext reqInputReferenceContext = new ReqInputReferenceContext(Qn.EMPTY, null);

      ReqInputPsiProcessingContext reqInputPsiProcessingContext =
          new ReqInputPsiProcessingContext(context, reqInputReferenceContext);

      ReqInputVarProjection vp = ReqInputProjectionsPsiParser.parseVarProjection(
          type,
          op,
          psi,
          resolver,
          reqInputPsiProcessingContext
      );

      reqInputReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });
  }

  public static ReqDeleteVarProjection parseReqDeleteVarProjection(
      @NotNull DataType type,
      @NotNull OpDeleteVarProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqDeleteVarProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_DELETE_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(context -> {
      ReqDeleteReferenceContext reqDeleteReferenceContext = new ReqDeleteReferenceContext(Qn.EMPTY, null);

      ReqDeletePsiProcessingContext reqDeletePsiProcessingContext = new ReqDeletePsiProcessingContext(context, reqDeleteReferenceContext);
      ReqDeleteVarProjection vp = ReqDeleteProjectionsPsiParser.parseVarProjection(
          type,
          op,
          psi,
          resolver,
          reqDeletePsiProcessingContext
      );

      reqDeleteReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });
  }

  public static OpInputVarProjection parseOpInputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(context -> {
      OpInputReferenceContext opInputReferenceContext = new OpInputReferenceContext(Qn.EMPTY, null);

      OpInputPsiProcessingContext opInputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, opInputReferenceContext);

      OpInputVarProjection vp = OpInputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          opInputPsiProcessingContext
      );

      opInputReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });

  }

  public static OpDeleteVarProjection parseOpDeleteVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpDeleteVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_DELETE_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(context -> {
      OpDeleteReferenceContext opDeleteReferenceContext = new OpDeleteReferenceContext(Qn.EMPTY, null);
      OpInputReferenceContext opInputReferenceContext = new OpInputReferenceContext(Qn.EMPTY, null);

      OpInputPsiProcessingContext opInputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, opInputReferenceContext);
      OpDeletePsiProcessingContext opDeletePsiProcessingContext =
          new OpDeletePsiProcessingContext(context, opInputPsiProcessingContext, opDeleteReferenceContext);

      OpDeleteVarProjection vp = OpDeleteProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          opDeletePsiProcessingContext
      );

      opDeleteReferenceContext.ensureAllReferencesResolved(context);
      opInputReferenceContext.ensureAllReferencesResolved(context);

      return vp;
    });

  }
}
