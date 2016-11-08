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
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.IdlPsiParser;
import ws.epigraph.idl.parser.IdlParserDefinition;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlFile;
import ws.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import ws.epigraph.idl.parser.psi.IdlOpVarPath;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.path.OpPathPrettyPrinter;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.path.ReqPathPrettyPrinter;
import ws.epigraph.projections.req.path.ReqVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.projections.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReadUrl;

import java.io.IOException;
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
    return Arrays.stream(lines)
                 .collect(Collectors.joining("\n"));
  }

  @NotNull
  public static Idl parseIdl(@NotNull String text, @NotNull TypesResolver resolver) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlFile psiFile =
        (IdlFile) EpigraphPsiUtil.parseFile("test.idl", text, IdlParserDefinition.INSTANCE, errorsAccumulator);

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      fail(DebugUtil.psiTreeToString(psiFile, true));
    }

    List<PsiProcessingError> errors = new ArrayList<>();
    Idl idl = null;

    try {
      idl = IdlPsiParser.parseIdl(psiFile, resolver, errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }
      fail();
    }

    assert idl != null;
    return idl;
  }

  @NotNull
  public static OpOutputVarProjection parseOpOutputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION.rootElementType(),
        IdlOpOutputVarProjection.class,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    List<PsiProcessingError> errors = new ArrayList<>();
    OpOutputVarProjection varProjection = null;
    try {
      varProjection = OpOutputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          errors
      );

    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }

      fail();
    }

    assert varProjection != null;
    return varProjection;
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
      boolean dumpPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH.rootElementType(),
        IdlOpVarPath.class,
        IdlSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    OpVarPath varPath = null;
    if (dumpPsiErrors) {
      try {
        varPath = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver);
      } catch (PsiProcessingException e) {
        e.printStackTrace();
        System.err.println(e.getMessage() + " at " + e.location());
        String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
        fail(psiDump);
      }
    } else {
      varPath = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver);
    }

    return varPath;
  }

  @NotNull
  public static UrlReadUrl parseReadUrl(@NotNull String url, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.READ_URL.rootElementType(),
        UrlReadUrl.class,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
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
  public static String printReqOutputVarProjection(@NotNull ReqOutputVarProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }
}
