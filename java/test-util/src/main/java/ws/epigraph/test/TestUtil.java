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

package ws.epigraph.test;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.util.PsiUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.gdata.GDataPrettyPrinter;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.IdlPrettyPrinter;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPrettyPrinter;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.path.OpPathPrettyPrinter;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.delete.ReqDeleteProjectionsPrettyPrinter;
import ws.epigraph.projections.req.delete.ReqDeleteVarProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.input.ReqInputProjectionsPrettyPrinter;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqPathPrettyPrinter;
import ws.epigraph.projections.req.path.ReqVarPath;
import ws.epigraph.projections.req.update.ReqUpdateProjectionsPrettyPrinter;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TestUtil {
  @NotNull
  public static String lines(@NotNull String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }

  // can't have this stuff here due to circular dependencies.. so copy-n-paste from here
  // todo find a way to fix this

  /*
  @NotNull
  public static Idl parseIdl(@NotNull String text, @NotNull TypesResolver resolver) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlFile psiFile =
        (IdlFile) EpigraphPsiUtil.parseFile("test.idl", text, IdlParserDefinition.INSTANCE, errorsAccumulator);

    failIfHasErrors(psiFile, errorsAccumulator);

    return runPsiParser(errors -> IdlPsiParser.parseIdl(psiFile, resolver, errors));
  }

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

    final PsiParserClosure<OpVarPath> closure =
        errors -> OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver, errors);

    return catchPsiErrors ? runPsiParser(closure) : runPsiParserNotCatchingErrors(closure);
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

  public static GDataValue parseGDataValue(String dataStr) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlDataValue dataValue = EpigraphPsiUtil.parseText(
        dataStr,
        IdlSubParserDefinitions.DATA_VALUE,
        errorsAccumulator
    );

    failIfHasErrors(dataValue, errorsAccumulator);

    return runPsiParser(errors -> IdlGDataPsiParser.parseValue(dataValue, errors));
  }

  @NotNull
  public static UrlReadUrl parseReadUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }

*/

  @NotNull
  public static String printOpVarPath(@NotNull OpVarPath path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpPathPrettyPrinter<NoExceptions> printer = new OpPathPrettyPrinter<>(layouter);
    printer.print(path, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqVarPath(@NotNull ReqVarPath path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqPathPrettyPrinter<NoExceptions> printer = new ReqPathPrettyPrinter<>(layouter);
    int len = ProjectionUtils.pathLength(path);
    printer.print(path, len);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqOutputFieldProjection(
      String fieldName,
      @NotNull ReqOutputFieldProjection projection,
      int pathSteps) {

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(fieldName, projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqOutputVarProjection(@NotNull ReqOutputVarProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqUpdateVarProjection(@NotNull ReqUpdateVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqUpdateProjectionsPrettyPrinter<NoExceptions> printer = new ReqUpdateProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqInputFieldProjection(String fieldName, @NotNull ReqInputFieldProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqInputProjectionsPrettyPrinter<NoExceptions> printer = new ReqInputProjectionsPrettyPrinter<>(layouter);
    printer.print(fieldName, projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqInputVarProjection(@NotNull ReqInputVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqInputProjectionsPrettyPrinter<NoExceptions> printer = new ReqInputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printReqDeleteVarProjection(@NotNull ReqDeleteVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqDeleteProjectionsPrettyPrinter<NoExceptions> printer = new ReqDeleteProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printOpInputVarProjection(@NotNull OpInputVarProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpInputProjectionsPrettyPrinter<NoExceptions> printer = new OpInputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printOpOutputVarProjection(@NotNull OpOutputVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpOutputProjectionsPrettyPrinter<NoExceptions> printer = new OpOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printOpDeleteVarProjection(OpDeleteVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpDeleteProjectionsPrettyPrinter<NoExceptions> printer = new OpDeleteProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printGDatum(GDatum gd) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    GDataPrettyPrinter<NoExceptions> printer = new GDataPrettyPrinter<>(layouter);
    printer.print(gd);
    layouter.close();
    return sb.getString();
  }

  @NotNull
  public static String printIdl(Idl idl) {
    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    IdlPrettyPrinter<NoExceptions> pp = new IdlPrettyPrinter<>(l);
    pp.print(idl);
    l.close();

    return sb.getString();
  }

  public static void failIfHasErrors(final List<PsiProcessingError> errors) {
    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) System.err.println(error.message() + " at " + error.location());

      fail();
    }
  }

  public static void failIfHasErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) {

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psi, true, false).trim();
      fail(psiDump);
    }
  }

  @NotNull
  public static <R> R runPsiParser(@NotNull TestUtil.PsiParserClosure<R> closure) {
    List<PsiProcessingError> errors = new ArrayList<>();
    R r = null;

    try {
      r = closure.runParser(errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    failIfHasErrors(errors);

    assert r != null;
    return r;
  }

  @NotNull
  public static <R> R runPsiParserNotCatchingErrors(@NotNull TestUtil.PsiParserClosure<R> closure)
      throws PsiProcessingException {
    List<PsiProcessingError> errors = new ArrayList<>();

    R r = closure.runParser(errors);

    if (!errors.isEmpty()) throw new PsiProcessingException("got parsing errors", PsiUtil.NULL_PSI_ELEMENT, errors);

    assert r != null;
    return r;
  }

  public interface PsiParserClosure<R> {
    R runParser(List<PsiProcessingError> errors) throws PsiProcessingException;
  }
}
