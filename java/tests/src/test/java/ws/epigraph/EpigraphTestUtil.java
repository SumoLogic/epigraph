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

package ws.epigraph;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import junit.framework.TestCase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.gdata.GData;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.input.OpInputReferenceContext;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.projections.req.ReqEntityProjection;
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
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputPsiProcessingContext;
import ws.epigraph.url.projections.req.output.ReqOutputReferenceContext;

import java.io.IOException;
import java.io.StringWriter;

import static junit.framework.TestCase.fail;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class EpigraphTestUtil {
  private EpigraphTestUtil() {}

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

    final GData gData = runPsiParser(true, context -> SchemaGDataPsiParser.parseData(dataPsi, context));

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

    return runPsiParser(true, context -> {
      OpInputReferenceContext opInputReferenceContext =
          new OpInputReferenceContext(ProjectionReferenceName.EMPTY, null, context);
      OpOutputReferenceContext opOutputReferenceContext =
          new OpOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpOutputPsiProcessingContext opOutputPsiProcessingContext = new OpOutputPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpOutputVarProjection vp = OpOutputProjectionsPsiParser.INSTANCE.parseVarProjection(
          varDataType,
          false,
          psiVarProjection,
          resolver,
          opOutputPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();
      opInputReferenceContext.ensureAllReferencesResolved();

      return vp;
    });

  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqOutputVarProjection(
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
      ReqOutputReferenceContext reqOutputReferenceContext =
          new ReqOutputReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      ReqOutputPsiProcessingContext reqOutputPsiProcessingContext =
          new ReqOutputPsiProcessingContext(context, reqOutputReferenceContext);

      @NotNull StepsAndProjection<ReqEntityProjection> res = ReqOutputProjectionPsiParser.INSTANCE.parseTrunkVarProjection(
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

  public static @NotNull String printOpOutputVarProjection(@NotNull OpOutputVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>> pctx = new
        ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>>(
            ProjectionReferenceName.EMPTY,
            null
        ) {
          @Override
          public boolean inNamespace(@NotNull ProjectionReferenceName projectionName) {
            return true;
          }
        };

    OpOutputProjectionsPrettyPrinter<NoExceptions> printer = new OpOutputProjectionsPrettyPrinter<>(layouter, pctx);
    printer.printVar(projection, 0);
    layouter.close();
    return sb.getString();
  }

  public static void checkEquals(@NotNull Data expected, @NotNull Data actual) {
    if (!expected.equals(actual)) {
      String expectedStr = printData(expected);
      String actualStr = printData(actual);

      if (expected.equals(actual))
        fail("Broken equals() implementation!");
      else
        TestCase.assertEquals(expectedStr, actualStr); // will show nice diff in idea
    }
  }

  public static void checkEquals(@NotNull Datum expected, @NotNull Datum actual) {
    if (!expected.equals(actual)) {
      String expectedStr = printDatum(expected);
      String actualStr = printDatum(actual);

      if (expected.equals(actual))
        fail("Broken equals() implementation!");
      else
        TestCase.assertEquals(expectedStr, actualStr); // will show nice diff in idea
    }
  }

  public static String printData(final Data data) {
    String dataToString;
    try {
      StringWriter sw = new StringWriter();
      DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
      printer.print(data);
      dataToString = sw.toString();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.toString());
      dataToString = null;
    }
    return dataToString;
  }

  @Contract("null -> !null")
  public static String printDatum(final Datum datum) {
    if (datum == null) return "null";
    String dataToString;
    try {
      StringWriter sw = new StringWriter();
      DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
      printer.print(datum.type(), datum);
      dataToString = sw.toString();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.toString());
      dataToString = null;
    }
    return dataToString;
  }
}
