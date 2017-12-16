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

package ws.epigraph.client.http;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpEntityProjection;
import ws.epigraph.tests.Person;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.ReqReferenceContext;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;

import java.net.URISyntaxException;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.server.http.Util.decodeUri;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UriComposerTest {
  private final TypesResolver resolver = StaticTypesResolver.instance();
  private final DataType dataType = new DataType(Person.type, null);

  private final OpProjection<?,?> personOpProjection = parsePersonOpProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String,",
          "    },",
          "    bestFriend :`record` (",
          "      id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    bestFriend2 $bf2 = :`record` ( id, bestFriend2 $bf2 ),",
          "    bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( id, bestFriend3 $bf3 ) ) ) ) ),",
          "    worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ),",
          "    friends *( :id ),",
          "    friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) ),",
          "    singleTagField :tag",
          "  )",
          ") :~(",
          "      ws.epigraph.tests.User :`record` (profile)",
          "        :~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
          "      ws.epigraph.tests.User2 :`record` (worstEnemy(id))",
          ")"
      )
  );

  @Test
  public void testComposeDeleteUri() throws URISyntaxException {
    String uri = UriComposer.composeDeleteUri(
        "foo",
        null,
        new StepsAndProjection<>(0, parseReqDeleteProjection("", true)),
        new StepsAndProjection<>(0, parseReqOutputProjection(":id", false))
    );

    assertEquals("/+foo:()>:id", decodeUri(uri));
  }

  private ReqFieldProjection parseReqDeleteProjection(String s, boolean flag) {
    return new ReqFieldProjection(
        parseReqDeleteProjection(
            dataType,
            personOpProjection,
            s,
            flag,
            resolver
        ).projection(),
        TextLocation.UNKNOWN
    );
  }

  private ReqFieldProjection parseReqOutputProjection(String s, boolean flag) {
    return new ReqFieldProjection(
        parseReqOutputProjection(
            dataType,
            personOpProjection,
            s,
            flag,
            resolver
        ).projection(),
        TextLocation.UNKNOWN
    );
  }

  private @NotNull OpProjection<?,?> parsePersonOpProjection(@NotNull String projectionString) {
    return parseOpProjection(dataType, projectionString, resolver);
  }

  public static @NotNull OpProjection<?,?> parseOpProjection(
      @NotNull DataType dataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpEntityProjection projectionPsi = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(projectionPsi, errorsAccumulator);

    return runPsiParser(true, context -> {
      OpReferenceContext opOutputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpProjection<?,?> p = new OpOutputProjectionsPsiParser(context).parseProjection(
          dataType,
          false,
          projectionPsi,
          resolver,
          opPsiProcessingContext
      );

      opOutputReferenceContext.ensureAllReferencesResolved();

      return p;
    });

  }

  public static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqOutputProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      boolean flag,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        ReqOutputProjectionPsiParser::new,
        type, op, projectionString, flag, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqDeleteProjection(
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      boolean flag,
      @NotNull TypesResolver resolver) {

    return parseReqProjection(
        ReqDeleteProjectionPsiParser::new,
        type, op, projectionString, flag, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqProjection(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
      @NotNull DataType type,
      @NotNull OpProjection<?,?> op,
      @NotNull String projectionString,
      boolean flag,
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
      @NotNull StepsAndProjection<ReqProjection<?,?>> res =
          parser.parseTrunkProjection(
              type,
              flag,
              op,
              psi,
              resolver,
              reqPsiProcessingContext
          );

      reqOutputReferenceContext.ensureAllReferencesResolved();

      return res;
    });
  }
}
