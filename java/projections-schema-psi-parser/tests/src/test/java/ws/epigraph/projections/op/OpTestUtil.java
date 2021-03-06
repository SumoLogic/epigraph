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

package ws.epigraph.projections.op;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.types.DataType;

import java.util.function.Function;

import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpTestUtil {
  private OpTestUtil() {}

  public static @NotNull OpProjection<?, ?> parseOpOutputProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpOutputProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  public static @NotNull OpProjection<?, ?> parseOpInputProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpInputProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  public static @NotNull OpProjection<?, ?> parseOpDeleteProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpDeleteProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  private static @NotNull OpProjection<?, ?> parseOpProjection(
      @NotNull Function<MessagesContext, OpProjectionPsiParser> parserFactory,
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PROJECTION,
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
      OpProjectionPsiParser parser = parserFactory.apply(context);
      OpProjection<?, ?> p = parser.parseProjection(
          varDataType,
          false,
          psiVarProjection,
          resolver,
          opPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();

      return p;
    });

  }

  public static @NotNull String printOpProjection(@NotNull OpProjection<?, ?> projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ProjectionsPrettyPrinterContext<OpProjection<?, ?>> pctx = new
        ProjectionsPrettyPrinterContext<OpProjection<?, ?>>(
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
}
