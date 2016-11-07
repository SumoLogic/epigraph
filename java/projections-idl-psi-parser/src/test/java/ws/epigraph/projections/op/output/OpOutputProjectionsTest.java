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

package ws.epigraph.projections.op.output;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.junit.Test;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  // todo add map tests when codegen is ready

  @Test
  public void testParsing() throws PsiProcessingException {
    // todo more elaborate example (multiple tails!).
    // Make pretty-printed result consistent with grammar?
    String projectionStr = lines(
        ":(",
        "  id,",
        "  record (",
        "    id {",
        "      ; +param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
        "    },",
        "    bestFriend :record (",
        "      id,",
        "      bestFriend: id",
        // todo get default tag from Person.type, once available
        "    ),",
        "    friends *( :id )",
//        "    friends *( :+id )",
//        "    friends { *( :+id ) }",
//        "    friends { { *( :+id {} ) } }",
//        "    friends { :_ { *( :+id {} ) } }", // same as above
        // :record (....) {params}
        "  )",
        ") ~ws.epigraph.tests.User :record (profile)"
    );


    String expected = lines(
        ":(",
        "  id,",
        "  record",
        "    (",
        "      id { ;+param1: epigraph.String = \"hello world\" { doc = \"some doc\" } },",
        "      bestFriend :record ( id, bestFriend :id ),",
        "      friends *( :id )",
        "    )",
        ") ~ws.epigraph.tests.User :record ( profile )"
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

  @Test
  public void testParseParam() throws PsiProcessingException {
    testParsingVarProjection(
        ":id { ;+param: map[epigraph.String,ws.epigraph.tests.Person] []( :id ) = ( \"foo\": < id: 123 > ) { deprecated = true } }"
    );
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(
        ":( id, record )"
    );
  }

  @Test
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "~ws.epigraph.tests.User :id"
        ,
        ":id ~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseDoubleTail() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "~ws.epigraph.tests.User :id ~ws.epigraph.tests.Person :id"
        ,
        ":id ~ws.epigraph.tests.User :id ~ws.epigraph.tests.Person :id"
    );
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "~( ws.epigraph.tests.User :id, ws.epigraph.tests.Person :id )"
        ,
        ":id ~( ws.epigraph.tests.User :id, ws.epigraph.tests.Person :id )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { deprecated = true }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend :record ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend { deprecated = true } :record ( id ) )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":record ( friendsMap [ forbidden, ;+param: epigraph.String, doc = \"no keys\" ]( :id ) )");
  }

  private void testParsingVarProjection(String str) throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str
        ,
        str
    );
  }

  private void testParsingVarProjection(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    OpOutputVarProjection varProjection = parseOpOutputVarProjection(varDataType, projectionString);

    String actual = print(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpOutputVarProjection parseOpOutputVarProjection(DataType varDataType, String projectionString) {
    TypesResolver
        resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type,
        String_Person_Map.type,
        epigraph.String.type
    );

    return TestUtil.parseOpOutputVarProjection(varDataType, projectionString, resolver);
  }

  private String print(OpOutputVarProjection projection) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpOutputProjectionsPrettyPrinter<NoExceptions> printer = new OpOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, 0);
    layouter.close();
    return sb.getString();
  }

}
