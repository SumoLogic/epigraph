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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  private final DataType dataType = new DataType(Person.type, Person.id);

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
        dataType,
        projectionStr,
        expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        dataType,
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
        "~ws.epigraph.tests.User :id"
        ,
        ":id ~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseDoubleTail() throws PsiProcessingException {
    final OpOutputVarProjection projection = testParsingVarProjection(
        dataType,
        "~ws.epigraph.tests.User :id ~ws.epigraph.tests.SubUser :id"
        ,
        ":id ~ws.epigraph.tests.User :id ~ws.epigraph.tests.SubUser :id"
    );

    List<OpOutputVarProjection> tails = projection.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    OpOutputVarProjection tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNotNull(tails);
    assertEquals(1, tails.size());
    tail = tails.get(0);

    tails = tail.polymorphicTails();
    assertNull(tails);
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
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

  @Test
  public void testTailsNormalization() throws PsiProcessingException {
    testTailsNormalization(
        ":record(id)~ws.epigraph.tests.User :record(profile)",
        User.type,
        ":record ( profile, id )"
    );

    // parameters merging
    testTailsNormalization(
        ":record(id,firstName{;param:epigraph.String})~ws.epigraph.tests.User :record(firstName{;param:epigraph.Integer})",
        User.type,
        ":record ( firstName { ;param: epigraph.Integer }, id )"
    );

    // annotations merging
    testTailsNormalization(
        ":record(id,firstName{doc=\"doc1\"})~ws.epigraph.tests.User :record(firstName{doc=\"doc2\"})",
        User.type,
        ":record ( firstName { doc = \"doc2\" }, id )"
    );

    testTailsNormalization(
        ":record(id)~ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName)",
        User.type,
        ":record ( firstName, id ) ~ws.epigraph.tests.SubUser :record ( lastName )"
    );

    testTailsNormalization(
        ":record(id)~ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName)",
        SubUser.type,
        ":record ( lastName, firstName, id ) ~ws.epigraph.tests.SubUser :record ( lastName )"
    );

    testTailsNormalization(
        ":record(id)~ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName)",
        User2.type,
        ":record ( id ) ~ws.epigraph.tests.User :record ( firstName ) ~ws.epigraph.tests.SubUser :record ( lastName )"
    );

    testTailsNormalization(
        ":record(id)~(ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName), ws.epigraph.tests.User2 :record(bestFriend:id))",
        User.type,
        ":record ( firstName, id ) ~ws.epigraph.tests.SubUser :record ( lastName )"
    );

    testTailsNormalization(
        ":record(id)~(ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName), ws.epigraph.tests.User2 :record(bestFriend:id))",
        SubUser.type,
        ":record ( lastName, firstName, id ) ~ws.epigraph.tests.SubUser :record ( lastName )"
    );

    testTailsNormalization(
        ":record(id)~(ws.epigraph.tests.User :record(firstName) ~ws.epigraph.tests.SubUser :record(lastName), ws.epigraph.tests.User2 :record(bestFriend:id))",
        User2.type,
        ":record ( bestFriend :id, id )"
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
        ":record(friendsMap[](:id))~ws.epigraph.tests.User :record(friendsMap[required, ;param:epigraph.String](:record(id)))",
        User.type,
        ":record ( friendsMap [ required, ;param: epigraph.String ]( :( record ( id ), id ) ) )"
    );
  }

  private void testTailsNormalization(String str, Type type, String expected) {
    OpOutputVarProjection varProjection = parseOpOutputVarProjection(dataType, str);
    final @NotNull OpOutputVarProjection normalized = varProjection.normalizedForType(type);
    String actual = printOpOutputVarProjection(normalized);
    assertEquals(expected, actual);
  }

  private void testParsingVarProjection(String str) {
    testParsingVarProjection(str, str);
  }

  private void testParsingVarProjection(String str, String expected) {
    testParsingVarProjection(dataType, str, expected);
  }

  private OpOutputVarProjection testParsingVarProjection(DataType varDataType, String projectionString, String expected) {

    OpOutputVarProjection varProjection = parseOpOutputVarProjection(varDataType, projectionString);

    String actual = printOpOutputVarProjection(varProjection);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());

    return varProjection;
  }

  private OpOutputVarProjection parseOpOutputVarProjection(DataType varDataType, String projectionString) {
    TypesResolver
        resolver = new SimpleTypesResolver(
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
        String_Person_Map.type,
        epigraph.String.type,
        epigraph.Integer.type
    );

    return parseOpOutputVarProjection(varDataType, projectionString, resolver);
  }

  private static @NotNull OpOutputVarProjection parseOpOutputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(errors -> OpOutputProjectionsPsiParser.parseVarProjection(
        varDataType,
        psiVarProjection,
        resolver,
        errors
    ));
  }

}
