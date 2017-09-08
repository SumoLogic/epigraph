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

package ws.epigraph.wire;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputReferenceContext;

import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class WireTestUtil {
  private WireTestUtil() {}

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

    return runPsiParser(true, context -> {
      OpInputReferenceContext inputReferenceContext =
          new OpInputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
      OpOutputReferenceContext outputReferenceContext =
          new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpInputPsiProcessingContext opInputPsiProcessingContext =
          new OpInputPsiProcessingContext(context, inputReferenceContext);
      OpOutputPsiProcessingContext outputPsiProcessingContext =
          new OpOutputPsiProcessingContext(context, opInputPsiProcessingContext, outputReferenceContext);

      OpOutputVarProjection vp = OpOutputProjectionsPsiParser.parseVarProjection(
          varDataType,
          false,
          psiVarProjection,
          resolver,
          outputPsiProcessingContext
      );

      outputReferenceContext.ensureAllReferencesResolved();
      inputReferenceContext.ensureAllReferencesResolved();
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

    return runPsiParser(true, context -> {
      ReqOutputReferenceContext referenceContext =
          new ReqOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
      ReqOutputPsiProcessingContext psiProcessingContext = new ReqOutputPsiProcessingContext(context, referenceContext);

      StepsAndProjection<ReqOutputVarProjection> res = ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
          type,
          false,
          op,
          psi,
          resolver,
          psiProcessingContext
      );

      referenceContext.ensureAllReferencesResolved();

      return res;
    });
  }
}
