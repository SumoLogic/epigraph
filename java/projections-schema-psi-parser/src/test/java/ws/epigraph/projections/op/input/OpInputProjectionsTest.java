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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.lang.Qn;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaSubParserDefinitions;
import ws.epigraph.schema.parser.psi.SchemaOpInputVarProjection;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsTest {
  @Test
  public void testParsing1() throws PsiProcessingException {
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
        ") ~~ws.epigraph.tests.User :`record` (profile)"
    );

    String expected = lines(
        ":( +id, `record` ( +id, +bestFriend :`record` ( +id, bestFriend :id { default: 123 } ), friends *( :+id ) ) )",
        "  ~~ws.epigraph.tests.User :`record` ( profile )"
    );

    testParsingVarProjection(
        projectionStr, expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        "", ":id"
    );
  }

  @Test
  public void testParseDefault() throws PsiProcessingException {
    testParsingVarProjection(":id { default: 123 }");
  }

  @Test
  public void testParseMultipleTags() throws PsiProcessingException {
    testParsingVarProjection(":( +id, `record` )");
  }

  @Test
  public void testParseRecursive() throws PsiProcessingException {
    testParsingVarProjection( "$self = :( id, `record` ( id, bestFriend $self ) )" );
  }

  @Test
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        "~~ws.epigraph.tests.User :id",
        ":id ~~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        "~~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )",
        ":id ~~( ws.epigraph.tests.User :id, ws.epigraph.tests.User2 :id )"
    );
  }

  @Test
  public void testParseModelTail() throws PsiProcessingException {
    testParsingVarProjection(
        ":`record` ( worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ) )"
    );
  }

  @Test
  public void testParseCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":id { deprecated = true }");
  }

  @Test
  public void testParseRecordDefaultFields() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( +id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( id, bestFriend :`record` ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( id, bestFriend :`record` { deprecated = true } ( id ) )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friendsMap [ ;param: epigraph.String, doc = \"bla\" ]( :id ) )");
  }

  @Test
  public void testParseMeta() throws PsiProcessingException {
    String projection = "{ meta: +( start, count ) } [ required ]( :`record` ( id, firstName ) )";

    testParsingVarProjection(
        new DataType(PersonMap.type, null),
        projection,
        projection
    );
  }


  private void testParsingVarProjection(String str) {
    testParsingVarProjection(
        str,
        str
    );
  }

  private void testParsingVarProjection(String str, String expected) {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str,
        expected
    );
  }

  private void testParsingVarProjection(
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
        epigraph.String.type
    );

    final OpInputVarProjection varProjection = parseOpInputVarProjection(
        varDataType,
        projectionString,
        resolver
    );
    String actual = printOpInputVarProjection(varProjection, 0);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  public static @NotNull OpInputVarProjection parseOpInputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    SchemaOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        SchemaSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(context -> {
      OpInputVarReferenceContext varReferenceContext = new OpInputVarReferenceContext(Qn.EMPTY, null);
      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(context, varReferenceContext);

      OpInputVarProjection vp =  OpInputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          inputPsiProcessingContext
      );

      varReferenceContext.ensureAllReferencesResolved(context);
      return vp;
    });

  }
}
