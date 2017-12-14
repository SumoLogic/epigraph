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
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityPath;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.schema.parser.psi.SchemaOpModelProjection;
import ws.epigraph.test.TestUtil;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.input.ReqInputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionPsiParser;
import ws.epigraph.util.Tuple2;

import java.util.function.Function;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class ReqTestUtil {
  private ReqTestUtil() {}

  public static OpProjection<?, ?> parseOpOutputProjection(
      @NotNull DataType entityDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpOutputProjectionsPsiParser::new, entityDataType, projectionString, resolver);
  }

  public static @NotNull OpModelProjection<?, ?, ?, ?> parseOpOutputModelProjection(
      @NotNull DatumTypeApi type,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpModelProjection(OpOutputProjectionsPsiParser::new, type, projectionString, resolver);
  }

  public static OpProjection<?, ?> parseOpInputProjection(
      @NotNull DataType dataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpInputProjectionsPsiParser::new, dataType, projectionString, resolver);
  }

  public static OpProjection<?, ?> parseOpUpdateProjection(
      @NotNull DataType dataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    // same parser for now
    return parseOpProjection(OpInputProjectionsPsiParser::new, dataType, projectionString, resolver);
  }

  public static OpProjection<?, ?> parseOpEntityProjection(
      @NotNull DataType dataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseOpProjection(OpDeleteProjectionsPsiParser::new, dataType, projectionString, resolver);
  }

  private static @NotNull OpProjection<?, ?> parseOpProjection(
      @NotNull Function<MessagesContext, OpProjectionPsiParser> parserFactory,
      @NotNull DataType entityDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityProjection entityProjectionPsi = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(entityProjectionPsi, errorsAccumulator);

    return runPsiParser(true, context -> {
      OpReferenceContext opOutputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpProjectionPsiParser parser = parserFactory.apply(context);
      OpProjection<?, ?> p = parser.parseProjection(
          entityDataType,
          false,
          entityProjectionPsi,
          resolver,
          opPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();

      return p;
    });

  }

  private static @NotNull OpModelProjection<?, ?, ?, ?> parseOpModelProjection(
      @NotNull Function<MessagesContext, OpProjectionPsiParser> parserFactory,
      @NotNull DatumTypeApi type,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpModelProjection modelProjectionPsi = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_MODEL_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(modelProjectionPsi, errorsAccumulator);

    return runPsiParser(true, context -> {
      OpReferenceContext opOutputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpProjectionPsiParser parser = parserFactory.apply(context);
      OpModelProjection<?, ?, ?, ?> mp = parser.parseModelProjection(
          type,
          false,
          modelProjectionPsi,
          resolver,
          opPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();

      return mp;
    });

  }

  public static StepsAndProjection<ReqProjection<?, ?>> parseReqOutputProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        ReqOutputProjectionPsiParser::new,
        type, op, projectionString, resolver
    );
  }

  public static StepsAndProjection<ReqProjection<?, ?>> parseReqInputProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        mc -> new ReqInputProjectionPsiParser(true, mc),
        type, op, projectionString, resolver
    );
  }

  public static StepsAndProjection<ReqProjection<?, ?>> parseReqUpdateProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        mc -> new ReqUpdateProjectionPsiParser(true, mc),
        type, op, projectionString, resolver
    );
  }

  public static StepsAndProjection<ReqProjection<?, ?>> parseReqDeleteProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        ReqDeleteProjectionPsiParser::new,
        type, op, projectionString, resolver
    );
  }

  public static StepsAndProjection<ReqProjection<?, ?>> parseReqProjection(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    return parseReqProjection2(parserFactory, type, op, projectionString, resolver)._1;
  }

  public static @NotNull Tuple2<StepsAndProjection<ReqProjection<?, ?>>, ReqReferenceContext> parseReqProjection2(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
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
      ReqReferenceContext reqOutputReferenceContext =
          new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      ReqPsiProcessingContext reqPsiProcessingContext =
          new ReqPsiProcessingContext(context, reqOutputReferenceContext);

      ReqProjectionPsiParser parser = parserFactory.apply(context);
      @NotNull StepsAndProjection<ReqProjection<?, ?>> res =
          parser.parseTrunkProjection(
              type,
              false,
              op,
              psi,
              resolver,
              reqPsiProcessingContext
          );

      reqOutputReferenceContext.ensureAllReferencesResolved();

      return Tuple2.of(res, reqOutputReferenceContext);
    });
  }

  public static @NotNull OpProjection<?, ?> parseOpPath(
      @NotNull DataType dataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    try {
      return parseOpPath(dataType, projectionString, true, resolver);
    } catch (PsiProcessingException e) { // can't happen..
      e.printStackTrace();
      fail(e.getMessage());
    }

    throw new RuntimeException("unreachable");
  }

  public static @NotNull OpProjection<?, ?> parseOpPath(
      @NotNull DataType dataType,
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

    final TestUtil.PsiParserClosure<OpProjection<?, ?>> closure = context -> {
      OpReferenceContext referenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext psiProcessingContext =
          new OpPsiProcessingContext(context, referenceContext);
      OpPathPsiProcessingContext opPathPsiProcessingContext =
          new OpPathPsiProcessingContext(context, psiProcessingContext);

      OpProjection<?, ?> p =
          OpPathPsiParser.parsePath(dataType, entityPathPsi, resolver, opPathPsiProcessingContext);

      referenceContext.ensureAllReferencesResolved();

      return p;
    };

    return catchPsiErrors ? runPsiParser(true, closure) : runPsiParserNotCatchingErrors(closure);
  }


}
