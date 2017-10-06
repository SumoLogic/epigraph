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

package ws.epigraph.url.projections.req.input;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.ReqTestUtil;
import ws.epigraph.url.projections.req.ReqReferenceContext;

import static org.junit.Assert.*;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputProjectionParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private final OpEntityProjection personOpProjection = parsePersonOpInputVarProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String",
          "    },",
          "    bestFriend :(+id, `record` (",
          "      +id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    )),",
          "    bestFriend2 $bf2 = :`record` ( id, bestFriend2 $bf2 ),",
          "    bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( id, bestFriend3 $bf3 ) ) ) ) ),",
          "    friends *( :id ),",
          "    friendsMap []( :(id, `record` (id, firstName) ) )",
          "  ) ~ws.epigraph.tests.UserRecord (profile)",
          ") :~ws.epigraph.tests.User :`record` (profile)"
      )
  );

  @Test
  public void testParseIdTag() {
    testParse(":id");
  }

  @Test
  public void testParseRecordTag() {
    testParse(":record");
  }

  @Test
  public void testParseMultiTag() {
    testParse(":(id,record)", ":( id, record )");
  }

  @Test
  public void testParseRecord() {
    testParse(":record ( id ;param1 = 'foo', friends *( :id ) )");
  }

  @Test
  public void testParseMap() {
    testParse(":record ( friendsMap [ '1', '2', '3' ]( :id ) )");
  }

  @Test
  public void testParseMapEmptyKeys() {
    testParse(":record ( friendsMap []( :id ) )");
  }

  @Test
  public void testParseList() {
    testParse(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseRecursiveWrongOp() {
    //noinspection ErrorNotRethrown
    try {
      testParse("$self = :( id, record ( id, bestFriend $self ) )");
      fail();
    } catch (AssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Tag 'id' is not supported"));
      assertTrue(e.getMessage(), e.getMessage().contains("Field 'bestFriend' is not supported"));
    }
  }

  @Test
  public void testParseRecursive() {
    testParse(":( id, record ( id, bestFriend2 $bf2 = :record ( id, bestFriend2 $bf2 ) ) )");
  }

  @Test
  public void testParseRecursiveDifferentOpRecursion() {
    testParse(":( id, record ( id, bestFriend3 $bf3 = :record ( id, bestFriend3 $bf3 ) ) )");
  }

  @Test
  public void testParseTail() {
    testParse(
        ":id :~User :record ( profile )",
        ":id :~ws.epigraph.tests.User :record ( profile )"
    );
  }

  @Test
  public void testParseModelTail() {
    testParse(
        ":record (id) ~UserRecord (profile)",
        ":record ( id ) ~ws.epigraph.tests.UserRecord ( profile )"
    );
  }

  // negative cases

  @Test
  public void testRequiredTag() {
    testParseFail(":record(bestFriend:record(id))");
  }

  @Test
  public void testRequiredField() {
    testParseFail(":record(bestFriend:(id,record()))");
  }

  @Test
  public void testRequiredPresent() {
    testParse(":record ( bestFriend :( id, record ( id ) ) )");
  }

  private void testParse(String expr) {
    testParse(expr, expr);
  }

  private void testParseFail(String expr) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqTrunkEntityProjection psi = EpigraphPsiUtil.parseText(
        expr,
        UrlSubParserDefinitions.REQ_ENTITY_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    try {
      TestUtil.runPsiParserNotCatchingErrors(context -> {

        ReqReferenceContext referenceContext = new ReqReferenceContext(
            ProjectionReferenceName.EMPTY, null, context
        );

        ReqPsiProcessingContext psiProcessingContext =
            new ReqPsiProcessingContext(context, referenceContext);

        @NotNull StepsAndProjection<ReqEntityProjection> vp =
            new ReqInputProjectionPsiParser(context).parseTrunkEntityProjection(
                dataType,
                false,
                personOpProjection,
                psi,
                resolver,
                psiProcessingContext
            );

        referenceContext.ensureAllReferencesResolved();

        return vp;
      });
      fail();
    } catch (PsiProcessingException ignored) {
    }
  }

  private void testParse(String expr, String expectedProjection) {
    final @NotNull StepsAndProjection<ReqEntityProjection> varProjection =
        ReqTestUtil.parseReqInputVarProjection(dataType, personOpProjection, expr, resolver);

    String s = TestUtil.printReqEntityProjection(varProjection);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);
  }

  private @NotNull OpEntityProjection parsePersonOpInputVarProjection(@NotNull String projectionString) {
    return ReqTestUtil.parseOpInputEntityProjection(dataType, projectionString, resolver);
  }
}
