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

package ws.epigraph.url.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.test.TestUtil.printReqOutputVarProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseOpOutputVarProjection;
import static ws.epigraph.url.projections.req.ReqTestUtil.parseReqOutputVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      User2.type,
      UserId2.type,
      UserRecord2.type,
      SubUser.type,
      SubUserId.type,
      SubUserRecord.type,
      epigraph.String.type
  );

  private final OpOutputVarProjection personOpProjection = parsePersonOpOutputVarProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
          "    },",
          "    firstName{;param:epigraph.String},",
          "    bestFriend :`record` (",
          "      id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    friends *( :(id,`record`(id)) ),",
          "    friendRecords * (id),",
          "    friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) )",
          "    friendRecordMap [] (id, firstName)",
          "  )",
          ") ~(",
          "      ws.epigraph.tests.User :`record` (profile)",
          "        ~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
          "      ws.epigraph.tests.User2 :`record` (worstEnemy(id))",
          ")"
      )
  );

//  private OpOutputFieldProjection personFieldProjection =

  @Test
  public void testParsePath() {
    testParse(":record / bestFriend :+record / bestFriend :record ( id, firstName )", 5);
  }

  @Test
  public void testParsePathMap() {
    testParse(":record / friendsMap / 'John' ;keyParam = 'foo' :record ( +firstName )", 5);
  }

  @Test
  public void testParseMap() {
    testParse(":record / friendsMap [ 'Alice', 'Bob' !sla = 100 ]( :id )", 3);
    testParse(
        ":record / friendsMap [ 'Alice', 'Bob' !sla = 100 ] :id",
        ":record / friendsMap [ 'Alice', 'Bob' !sla = 100 ]( :id )",
        3
    );
    testParse(
        ":record / friendRecordMap [ ] ( id )",
        ":record / friendRecordMap [ ]( ( id ) )",
        3
    );
  }

  @Test
  public void testParseMapStar() {
    testParse(":record / friendsMap [ * ]( :id )", 3);
  }

  @Test
  public void testParseList() {
    testParse(":record / friends *( :id )", 3);
    testParse(":record / friends * :id", ":record / friends *( :id )", 3);
    testParse(":record / friendRecords * (id)", ":record / friendRecords *( ( id ) )", 3);
  }

  @Test
  public void testParseParam() {
    testParse(":( id, record ( id ;param1 = 'foo' ) )", 0);
  }

  @Test
  public void testParseParamDefault() {
    testParse(":( id, record ( id ) )", 0); // defaults are not substituted!
  }

  @Test
  public void testParseTail() {
    testParse(
        ":id ~User :record ( profile )",
        ":id ~ws.epigraph.tests.User :record ( profile )",
        1
    );
  }

  @Test
  public void testParseDoubleTail() {
    testParse(
        ":id ~User :record ( profile ) ~SubUser :record (worstEnemy(id))",
        ":id ~ws.epigraph.tests.User :record ( profile ) ~ws.epigraph.tests.SubUser :record ( worstEnemy ( id ) )",
        1
    );
  }

  @Test
  public void testStarTags() {
    testParse(
        ":*",
        ":( id, record )",
        0
    );
  }

  @Test
  public void testStarTags2() {
    testParse(
        ":record(bestFriend:*)",
        ":record ( bestFriend :( record ) )",
        1
    );
  }

  @Test
  public void testStarFields() {
    testParse(
        ":record(*)",
        ":record ( id, firstName, bestFriend :(), friends *( :() ), friendRecords, friendsMap [ * ]( :() ), friendRecordMap )",
        1
    );
  }

  @Test
  public void testTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":record(id)~ws.epigraph.tests.User :record(firstName)",
        User.type,
        ":record ( firstName, id )"
    );

    // parameters merging
    testTailsNormalization(
        ":record(id,firstName;param='foo')~ws.epigraph.tests.User :record(firstName;param='bar')",
        User.type,
        ":record ( firstName ;param = \"bar\", id )"
    );

    // annotations merging
    testTailsNormalization(
        ":record(id,firstName!doc='doc1')~ws.epigraph.tests.User :record(firstName!doc='doc2')",
        User.type,
        ":record ( firstName !doc = \"doc2\", id )"
    );
  }

  @Test
  public void testListTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":record(friends*(:id))~ws.epigraph.tests.User :record(friends*(:record(id)))",
        User.type,
        ":record ( friends *( :( record ( id ), id ) ) )"
    );
  }

  @Test
  public void testMapTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":record(friendsMap[1](:id))~ws.epigraph.tests.User :record(friendsMap[2 ;keyParam='foo'](:record(id)))",
        User.type,
        ":record ( friendsMap [ \"2\" ;keyParam = \"foo\", \"1\" ]( :( record ( id ), id ) ) )"
    );

    testTailsNormalization(
        ":record(friendsMap[2 ;keyParam='bar'](:id))~ws.epigraph.tests.User :record(friendsMap[2 ;keyParam='foo'](:record(id)))",
        User.type,
        ":record ( friendsMap [ \"2\" ;keyParam = \"foo\" ]( :( record ( id ), id ) ) )"
    );
  }

  // todo negative test cases too

  private void testTailsNormalization(String str, Type type, String expected) {
    final @NotNull StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
        parseReqOutputVarProjection(dataType, personOpProjection, str, resolver);

    ReqOutputVarProjection varProjection = stepsAndProjection.projection();
    final @NotNull ReqOutputVarProjection normalized = varProjection.normalizedForType(type);

    String actual = printReqOutputVarProjection(normalized, stepsAndProjection.pathSteps());
    assertEquals(expected, actual);
  }

  private void testParse(String expr, int steps) {
    testParse(expr, expr, steps);
  }

  private ReqOutputVarProjection testParse(String expr, String expectedProjection, int steps) {
    final @NotNull StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
        parseReqOutputVarProjection(dataType, personOpProjection, expr, resolver);

    assertEquals(steps, stepsAndProjection.pathSteps());

    String s = printReqOutputVarProjection(stepsAndProjection.projection(), steps);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);

    return stepsAndProjection.projection();
  }

  private @NotNull OpOutputVarProjection parsePersonOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString, resolver);
  }

}
