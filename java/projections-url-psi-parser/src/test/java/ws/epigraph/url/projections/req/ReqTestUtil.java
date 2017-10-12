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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpProjectionPsiParser;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityPath;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.test.TestUtil;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.input.ReqInputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionPsiParser;

import java.util.function.Function;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class ReqTestUtil {
  private ReqTestUtil() {}

  public static @NotNull OpEntityProjection parseOpOutputEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpOutputProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  public static @NotNull OpEntityProjection parseOpInputEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpInputProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  public static @NotNull OpEntityProjection parseOpDeleteEntityProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpEntityProjection(OpDeleteProjectionsPsiParser::new, varDataType, projectionString, resolver);
  }

  private static @NotNull OpEntityProjection parseOpEntityProjection(
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

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqOutputEntityProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        ReqOutputProjectionPsiParser::new,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqInputEntityProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        mc -> new ReqInputProjectionPsiParser(true, mc),
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqUpdateEntityProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        mc -> new ReqUpdateProjectionPsiParser(true, mc),
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqDeleteVarProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        ReqDeleteProjectionPsiParser::new,
        type, op, projectionString, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqEntityProjection(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
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
      ReqReferenceContext reqOutputReferenceContext =
          new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      ReqPsiProcessingContext reqPsiProcessingContext =
          new ReqPsiProcessingContext(context, reqOutputReferenceContext);

      ReqProjectionPsiParser parser = parserFactory.apply(context);
      @NotNull StepsAndProjection<ReqEntityProjection> res =
          parser.parseTrunkEntityProjection(
              type,
              false,
              op,
              psi,
              resolver,
              reqPsiProcessingContext
          );

      reqOutputReferenceContext.ensureAllReferencesResolved();

      return res;
    });
  }

  public static @NotNull OpEntityProjection parseOpEntityPath(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    try {
      return parseOpEntityPath(varDataType, projectionString, true, resolver);
    } catch (PsiProcessingException e) { // can't happen..
      e.printStackTrace();
      fail(e.getMessage());
    }

    throw new RuntimeException("unreachable");
  }

  public static @NotNull OpEntityProjection parseOpEntityPath(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      boolean catchPsiErrors,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityPath entityPathPsi = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PATH,
        errorsAccumulator
    );

    failIfHasErrors(entityPathPsi, errorsAccumulator);

    final TestUtil.PsiParserClosure<OpEntityProjection> closure = context -> {
      OpReferenceContext referenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext psiProcessingContext =
          new OpPsiProcessingContext(context, referenceContext);
      OpPathPsiProcessingContext opPathPsiProcessingContext =
          new OpPathPsiProcessingContext(context, psiProcessingContext);

      OpEntityProjection vp = OpPathPsiParser.parseEntityPath(varDataType, entityPathPsi, resolver, opPathPsiProcessingContext);

      referenceContext.ensureAllReferencesResolved();

      return vp;
    };

    return catchPsiErrors ? runPsiParser(true, closure) : runPsiParserNotCatchingErrors(closure);
  }


}
