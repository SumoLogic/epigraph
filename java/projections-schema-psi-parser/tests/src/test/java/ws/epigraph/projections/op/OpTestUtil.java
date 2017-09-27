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
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.types.DataType;

import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpTestUtil {
  private OpTestUtil() {}

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
      OpEntityProjection vp = parser.parseEntityProjection(
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
    printer.printVar(projection, 0);
    layouter.close();
    return sb.getString();
  }
}
