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

package ws.epigraph.projections.op.input;

import org.junit.Test;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.op.OpTestUtil;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsTest {
  @Test
  public void testParsing1() {
    String projectionStr = lines(
        ":(",
        "  +id,",
        "  `record` (",
        "    +id,",
        "    +bestFriend :`record` (",
        "      +id,",
        "      bestFriend: id { default: 123 }",
        "    ),",
        "    friends :_ {} *( :+id {} )",
        "  )",
        ") :~ws.epigraph.tests.User :`record` (profile)"
    );

    String expected = lines(
        ":( +id, `record` ( +id, +bestFriend :`record` ( +id, bestFriend :id { default: 123 } ), friends *( :+id ) ) )",
        "  :~ws.epigraph.tests.User :`record` ( profile )"
    );

    testParsingProjection(
        projectionStr, expected
    );
  }

  @Test
  public void testParseEmpty() {
    testParsingProjection(
        "", ":id"
    );
  }

  @Test
  public void testParseDefault() {
    testParsingProjection(":id { default: 123 }");
  }

  @Test
  public void testParseDefaultRequired() {
    testParsingProjection(":`record` { default: { firstName: \"John\" } } ( id, firstName )");

    try {
      testParsingProjection(":`record` { default: { firstName: \"John\" } } ( +id, firstName )");
      fail("This should fail with 'Required field missing' message");
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError error) {
      String message = error.getMessage();
      assertTrue(error.toString(), message.contains("Required field 'id' is missing"));
    }
  }

  @Test
  public void testParseMultipleTags() {
    testParsingProjection(":( +id, `record` )");
  }

  @Test
  public void testParseRecursive() {
    testParsingProjection("$self = :( id, `record` ( id, bestFriend $self ) )");
  }

  @Test
  public void testParseModelRecursive() {
    testParsingProjection(":`record` $rr = ( id, bestFriend :`record` $rr )");
  }

  @Test
  public void testParseTail() {
    testParsingProjection(
        ":~ws.epigraph.tests.User :id",
        ":id :~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseTails() {
    testParsingProjection(
        ":~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )",
        ":id :~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
    );
  }

  @Test
  public void testParseModelTail() {
    testParsingProjection(
        ":`record` ( worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ) )"
    );
  }

  @Test
  public void testParseCustomParams() {
    testParsingProjection(":id { @epigraph.annotations.Deprecated }");
  }

  @Test
  public void testParseRecordDefaultFields() {
    testParsingProjection(":`record` ( +id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() {
    testParsingProjection(":`record` ( id, bestFriend :`record` ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() {
    testParsingProjection(":`record` ( id, bestFriend :`record` { @epigraph.annotations.Deprecated } ( id ) )");
  }

  @Test
  public void testParseList() {
    testParsingProjection(":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() {
    testParsingProjection(
        ":`record` ( friendsMap [ ;param: epigraph.String, @epigraph.annotations.Doc \"bla\" ]( :id ) )");
  }

  @Test
  public void testParseMapWithKeyProjection() {
    testParsingProjection(
        lines(
            ":`record` (",
            "  personRecToPersonRec [ ;param: epigraph.String, @epigraph.annotations.Doc \"bla\", projection: ( firstName, lastName ) ](",
            "    ( firstName ) )",
            ")"
        )
    );
  }

  @Test
  public void testParseMeta() {
    String projection = "{ meta: +( start, count ) } [ required ]( :`record` ( id, firstName ) )";

    testParsingProjection(
        new DataType(PersonMap.type, null),
        projection,
        projection
    );
  }


  private void testParsingProjection(String str) {
    testParsingProjection(
        str,
        str
    );
  }

  private void testParsingProjection(String str, String expected) {
    testParsingProjection(
        new DataType(Person.type, Person.id),
        str,
        expected
    );
  }

  private void testParsingProjection(
      DataType varDataType,
      String projectionString,
      String expected) {

    TypesResolver resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        User2.type,
        UserId.type,
        UserRecord.type,
        PaginationInfo.type,
        PersonMap.type,
        epigraph.String.type,
        epigraph.annotations.Deprecated.type,
        epigraph.annotations.Doc.type
    );

    final OpProjection<?, ?> projection = OpTestUtil.parseOpInputProjection(
        varDataType,
        projectionString,
        resolver
    );

    String actual = OpTestUtil.printOpProjection(projection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

}
