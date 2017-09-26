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

package ws.epigraph.projections.op.delete;

import org.junit.Test;
import ws.epigraph.projections.op.OpTestUtil;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsTest {
  @Test
  public void testParsing() throws PsiProcessingException {
    // Make pretty-printed result consistent with grammar?
    String projectionStr = lines(
        ":(",
        "  id,",
        "  `record` (",
        "    id {",
        "      ; +param1 : epigraph.String { @epigraph.annotations.Doc \"some doc\", default : \"hello world\" },",
        "    },",
        "    +bestFriend :`record` (",
        "      id,",
        "      bestFriend: id",
        "      bestFriend2: id",
        "    ),",
        "    friends *+( :id )",
        "  )",
        ")"
    );


    String expected = lines(
        ":(",
        "  id,",
        "  `record` (",
        "    +id { ;+param1: epigraph.String { @epigraph.annotations.Doc \"some doc\", default: \"hello world\" } },",
        "    +bestFriend :`record` ( +id, bestFriend :id, +bestFriend2 :id ),",
        "    friends *+( :id )",
        "  )",
        ")"
    );

    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        projectionStr,
        expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        ""
        ,
        ":id"
    );
  }

//  @Test
//  public void testParseEmptyPlus() throws PsiProcessingException {
//    testParsingVarProjection(
//        new DataType(Person.type, Person.id),
//        "+"
//        ,
//        "+:id"
//    );
//  }

  @Test
  public void testParseParam() throws PsiProcessingException {
    testParsingVarProjection(
        lines(
            ":id {",
            "  ;+param: map[epigraph.String,ws.epigraph.tests.Person]",
            "    { @epigraph.annotations.Deprecated, default: ( \"foo\": < id: 123 > ) } [ ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseParam2() throws PsiProcessingException {
    testParsingVarProjection(
        ":id { ;+param: ws.epigraph.tests.UserRecord { default: { id: 1 } } }"
    );
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(
        ":( id, `record` )"
    );
  }

  @Test
  public void testParseSimpleField() throws PsiProcessingException {
    testParsingVarProjection(
        ":( `record` ( id ) )",
        ":( `record` ( +id ) )"
    );
  }

  @Test
  public void testParseRecursive() throws PsiProcessingException {
    testParsingVarProjection("$self = :( id, `record` ( +id, bestFriend $self ) )");
  }

  @Test
  public void testParseModelRecursive() throws PsiProcessingException {
    testParsingVarProjection(":`record` $rr = ( +id, bestFriend :`record` $rr )");
  }

  @Test
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        ":~ws.epigraph.tests.User :id",
        ":id :~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        ":~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )",
        ":id :~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
    );
  }

  @Test
  public void testParseModelTail() throws PsiProcessingException {
    testParsingVarProjection(
        ":`record` ( worstEnemy ( +id ) ~ws.epigraph.tests.UserRecord ( +profile ) )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { @epigraph.annotations.Deprecated }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( +id, +firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( +id, +bestFriend :`record` ( +id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( +id, bestFriend :`record` { @epigraph.annotations.Deprecated } ( +id ) )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends *+( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(
        ":`record` ( friendsMap [ forbidden, ;+param: epigraph.String, @epigraph.annotations.Doc \"no keys\" ]+( :id ) )");
  }

  private void testParsingVarProjection(String str) {
    testParsingVarProjection(str, str);
  }

  private void testParsingVarProjection(String str, String exp) {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str
        ,
        exp
    );
  }

  private void testParsingVarProjection(DataType varDataType, String projectionString, String expected) {

    OpOutputVarProjection varProjection = parseOpDeleteVarProjection(varDataType, projectionString);

    String actual = OpTestUtil.printOpEntityProjection(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpOutputVarProjection parseOpDeleteVarProjection(DataType varDataType, String projectionString) {
    TypesResolver
        resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        User2.type,
        UserId.type,
        UserRecord.type,
        String_Person_Map.type,
        epigraph.String.type,
        epigraph.annotations.Doc.type,
        epigraph.annotations.Deprecated.type
    );

    return OpTestUtil.parseOpDeleteVarProjection(
        varDataType,
        projectionString,
        resolver
    );
  }

}
