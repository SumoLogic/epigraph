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

package ws.epigraph.validation.data;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.gdata.GData;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputPsiProcessingContext;
import ws.epigraph.projections.op.output.OpOutputReferenceContext;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaData;
import ws.epigraph.schema.parser.psi.SchemaDataValue;
import ws.epigraph.schema.parser.psi.SchemaOpOutputVarProjection;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionsPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputReferenceContext;

import static junit.framework.TestCase.fail;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DataValidatorTestUtil {
  private DataValidatorTestUtil() {}

  public static @NotNull Data makeData(@NotNull Type type, @NotNull String s, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    final SchemaDataValue dataValuePsi = EpigraphPsiUtil.parseText(
        s,
        SchemaSubParserDefinitions.DATA_VALUE,
        errorsAccumulator
    );

    failIfHasErrors(dataValuePsi, errorsAccumulator);

    final SchemaData dataPsi = dataValuePsi.getData();
    assert dataPsi != null;

    final GData gData = runPsiParser(context -> SchemaGDataPsiParser.parseData(dataPsi, context));

    try {
      return GDataToData.transform(type, gData, resolver);
    } catch (GDataToData.ProcessingException e) {
      fail(e.toString());
      return null;
    }
  }
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
      OpInputReferenceContext opInputReferenceContext =
          new OpInputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
      OpOutputReferenceContext opOutputReferenceContext =
          new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);

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

      opOutputReferenceContext.ensureAllReferencesResolved();
      opInputReferenceContext.ensureAllReferencesResolved();

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
      ReqOutputReferenceContext reqOutputReferenceContext =
          new ReqOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);

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

      reqOutputReferenceContext.ensureAllReferencesResolved();

      return res;
    });
  }
}
