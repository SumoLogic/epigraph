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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpDeleteVarProjection;
import ws.epigraph.idl.parser.psi.IdlOpInputVarProjection;
import ws.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import ws.epigraph.idl.parser.psi.IdlOpVarPath;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.delete.ReqDeleteVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqDeleteVarProjection;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.parser.psi.UrlReqUpdateVarProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionsPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionsPsiParser;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqTestUtil {
  @NotNull
  public static OpOutputVarProjection parseOpOutputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(errors -> OpOutputProjectionsPsiParser.parseVarProjection(
        varDataType,
        psiVarProjection,
        resolver,
        errors
    ));
  }

  @NotNull
  public static StepsAndProjection<ReqOutputVarProjection> parseReqOutputVarProjection(
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

    return runPsiParser(errors -> ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
        type,
        op,
        psi,
        resolver,
        errors
    ));
  }

  @NotNull
  public static OpVarPath parseOpVarPath(
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

  @NotNull
  public static OpVarPath parseOpVarPath(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      boolean catchPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    final TestUtil.PsiParserClosure<OpVarPath> closure =
        errors -> OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver, errors);

    return catchPsiErrors ? runPsiParser(closure) : runPsiParserNotCatchingErrors(closure);
  }

  @NotNull
  public static ReqUpdateVarProjection parseReqUpdateVarProjection(
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

    return runPsiParser(errors -> ReqUpdateProjectionsPsiParser.parseVarProjection(
        type,
        op,
        psi,
        resolver,
        errors
    ));
  }
  @NotNull
  
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

    return runPsiParser(errors -> ReqDeleteProjectionsPsiParser.parseVarProjection(
        type,
        op,
        psi,
        resolver,
        errors
    ));
  }

  public static StepsAndProjection<OpInputVarProjection> parseOpInputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(errors -> OpInputProjectionsPsiParser.parseVarProjection(
        varDataType,
        psiVarProjection,
        resolver,
        errors
    ));

  }

  public static OpDeleteVarProjection parseOpDeleteVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpDeleteVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_DELETE_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(errors -> OpDeleteProjectionsPsiParser.parseVarProjection(
        varDataType,
        psiVarProjection,
        resolver,
        errors
    ));

  }
}
