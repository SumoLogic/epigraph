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
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.ReqReferenceContext;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;

import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class WireTestUtil {
  private WireTestUtil() {}

  public static @NotNull OpProjection<?, ?> parseOpProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityProjection psiEntityProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiEntityProjection, errorsAccumulator);

    return runPsiParser(true, context -> {
      OpReferenceContext outputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext outputPsiProcessingContext =
          new OpPsiProcessingContext(context, outputReferenceContext);

      OpProjection<?, ?> p = new OpOutputProjectionsPsiParser(context).parseProjection(
          varDataType,
          false,
          psiEntityProjection,
          resolver,
          outputPsiProcessingContext
      );

      outputReferenceContext.ensureAllReferencesResolved();
      return p;

    });
  }

  public static @NotNull StepsAndProjection<ReqProjection<?, ?>> parseReqProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?, ?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqTrunkEntityProjection psi = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    return runPsiParser(true, context -> {
      ReqReferenceContext referenceContext =
          new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
      ReqPsiProcessingContext psiProcessingContext = new ReqPsiProcessingContext(context, referenceContext);

      @NotNull StepsAndProjection<ReqProjection<?, ?>> res =
          new ReqOutputProjectionPsiParser(context).parseTrunkProjection(
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
