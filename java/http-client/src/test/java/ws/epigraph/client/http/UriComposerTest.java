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
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpPsiProcessingContext;
import ws.epigraph.projections.op.OpReferenceContext;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
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
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UriComposerTest {
  private final TypesResolver resolver = StaticTypesResolver.instance();
  private final DataType dataType = new DataType(Person.type, null);

  private final OpEntityProjection personOpProjection = parsePersonOpEntityProjection(
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
        new StepsAndProjection<>(0, parseReqDeleteEntityProjection("", true)),
        new StepsAndProjection<>(0, parseReqOutputEntityProjection(":id", false))
    );

    assertEquals("/+foo:()>:id", decodeUri(uri));
  }

  private ReqFieldProjection parseReqDeleteEntityProjection(String s, boolean flag) {
    return new ReqFieldProjection(
        parseReqDeleteEntityProjection(
            dataType,
            personOpProjection,
            s,
            flag,
            resolver
        ).projection(),
        TextLocation.UNKNOWN
    );
  }

  private ReqFieldProjection parseReqOutputEntityProjection(String s, boolean flag) {
    return new ReqFieldProjection(
        parseReqOutputEntityProjection(
            dataType,
            personOpProjection,
            s,
            flag,
            resolver
        ).projection(),
        TextLocation.UNKNOWN
    );
  }

  private @NotNull OpEntityProjection parsePersonOpEntityProjection(@NotNull String projectionString) {
    return parseOpEntityProjection(dataType, projectionString, resolver);
  }

  public static @NotNull OpEntityProjection parseOpEntityProjection(
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
      OpReferenceContext opOutputReferenceContext =
          new OpReferenceContext(ProjectionReferenceName.EMPTY, null, context);

      OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
          context,
          opOutputReferenceContext
      );
      OpEntityProjection vp = new OpOutputProjectionsPsiParser(context).parseEntityProjection(
          varDataType,
          false,
          psiEntityProjection,
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
      boolean flag,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        ReqOutputProjectionPsiParser::new,
        type, op, projectionString, flag, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqDeleteEntityProjection(
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
      @NotNull String projectionString,
      boolean flag,
      @NotNull TypesResolver resolver) {

    return parseReqEntityProjection(
        ReqDeleteProjectionPsiParser::new,
        type, op, projectionString, flag, resolver
    );
  }

  public static @NotNull StepsAndProjection<ReqEntityProjection> parseReqEntityProjection(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
      @NotNull DataType type,
      @NotNull OpEntityProjection op,
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
      @NotNull StepsAndProjection<ReqEntityProjection> res =
          parser.parseTrunkEntityProjection(
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
