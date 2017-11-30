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

package ws.epigraph.test;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.util.PsiUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDataPrettyPrinter;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.op.OpPathPrettyPrinter;
import ws.epigraph.projections.op.OpProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqPathPrettyPrinter;
import ws.epigraph.psi.*;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.SchemaPrettyPrinter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class TestUtil {
  private TestUtil() {}

  public static @NotNull String lines(@NotNull String... lines) {
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
  public static OpEntityProjection parseOpEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    try {
      return parseOpEntityProjection(varDataType, projectionString, true, resolver);
    } catch (PsiProcessingException e) { // can't happen..
      e.printStackTrace();
      fail(e.getMessage());
    }

    throw new RuntimeException("unreachable");
  }

  @NotNull
  public static OpEntityProjection parseOpEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      boolean catchPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpEntityProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    final PsiParserClosure<OpEntityProjection> closure =
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

  public static @NotNull String printOpEntityPath(@NotNull OpEntityProjection path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpPathPrettyPrinter<NoExceptions> printer = new OpPathPrettyPrinter<>(layouter);
    printer.printProjection(path, 0);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printReqEntityPath(@NotNull ReqEntityProjection path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqPathPrettyPrinter<NoExceptions> printer = new ReqPathPrettyPrinter<>(layouter);
    int len = ProjectionUtils.pathLength(path);
    printer.printProjection(path, len);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printReqFieldProjection(
      String fieldName,
      @NotNull ReqFieldProjection projection,
      int pathSteps) {

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqProjectionsPrettyPrinter<NoExceptions> printer = new ReqProjectionsPrettyPrinter<>(layouter);
    printer.print(fieldName, projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printReqEntityProjection(@NotNull StepsAndProjection<ReqEntityProjection> stepsAndProjection) {
    return printReqEntityProjection(stepsAndProjection.projection(), stepsAndProjection.pathSteps());
  }

  public static @NotNull String printReqEntityProjection(@NotNull ReqEntityProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqProjectionsPrettyPrinter<NoExceptions> printer = new ReqProjectionsPrettyPrinter<>(layouter);
    printer.printProjection(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printOpEntityProjection(@NotNull OpEntityProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> pctx = new
        ProjectionsPrettyPrinterContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>>(
            ProjectionReferenceName.EMPTY,
            null
        ) {
          @Override
          public boolean inNamespace(@Nullable ProjectionReferenceName projectionName) {
            return true;
          }
        };

    OpProjectionsPrettyPrinter<NoExceptions> printer = new OpProjectionsPrettyPrinter<>(layouter, pctx);
    printer.printProjection(projection, 0);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printOpModelProjection(@NotNull OpModelProjection<?, ?, ?, ?> projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpProjectionsPrettyPrinter<NoExceptions> printer = new OpProjectionsPrettyPrinter<>(layouter);
    printer.printModel(projection, 0);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printGDatum(GDatum gd) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    GDataPrettyPrinter<NoExceptions> printer = new GDataPrettyPrinter<>(layouter);
    printer.print(gd);
    layouter.close();
    return sb.getString();
  }

  public static @NotNull String printSchema(ResourcesSchema schema) {
    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    SchemaPrettyPrinter<NoExceptions> pp = new SchemaPrettyPrinter<>(l);
    pp.print(schema);
    l.close();

    return sb.getString();
  }

  public static void failIfHasErrors(boolean failOnWarnings, final List<PsiProcessingMessage> messages) {
    if (!messages.isEmpty()) {
      boolean fatal = false;
      StringBuilder sb = new StringBuilder("\n");
      for (final PsiProcessingMessage message : messages) {

        if (message.level() == PsiProcessingMessage.Level.ERROR)
          fatal = true;

        sb.append(message.location()).append(": ").append(message.message()).append("\n");
      }

      if (fatal || failOnWarnings)
        fail(sb.toString());
//      else
//        System.out.print(sb.toString());
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

  public static @NotNull <R> R runPsiParser(boolean failOnWarnings, @NotNull TestUtil.PsiParserClosure<R> closure) {
    PsiProcessingContext context = new DefaultPsiProcessingContext();
    R r = null;

    try {
      r = closure.runParser(context);
    } catch (PsiProcessingException e) {
      context.setMessages(e.messages());
    }

    failIfHasErrors(failOnWarnings, context.messages());

    assert r != null;
    return r;
  }

  public static @NotNull <R> R runPsiParserNotCatchingErrors(@NotNull TestUtil.PsiParserClosure<R> closure)
      throws PsiProcessingException {
    PsiProcessingContext context = new DefaultPsiProcessingContext();

    R r = closure.runParser(context);

    final List<PsiProcessingMessage> errors = context.messages();
    if (!errors.isEmpty()) throw new PsiProcessingException("got parsing errors", PsiUtil.NULL_PSI_ELEMENT, errors);

    assert r != null;
    return r;
  }

  public interface PsiParserClosure<R> {
    R runParser(PsiProcessingContext context) throws PsiProcessingException;
  }
}
