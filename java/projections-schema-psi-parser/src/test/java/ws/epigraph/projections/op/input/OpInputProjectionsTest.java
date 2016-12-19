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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.EdlSubParserDefinitions;
import ws.epigraph.schema.parser.psi.EdlOpInputVarProjection;
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
        "    friends {} :_ {} *( :+id {} )",
        "  )",
        ") ~ws.epigraph.tests.User :`record` (profile)"
    );

    String expected = lines(
        ":( +id, `record` ( +id, +bestFriend :`record` ( +id, bestFriend :id { default: 123 } ), friends *( :+id ) ) )",
        "  ~ws.epigraph.tests.User :`record` ( profile )"
    );

    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        projectionStr, expected
    );
  }

  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
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
  public void testParseTail() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "~ws.epigraph.tests.User :id",
        ":id ~ws.epigraph.tests.User :id"
    );
  }

  @Test
  public void testParseTails() throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        "~( ws.epigraph.tests.User :id, ws.epigraph.tests.Person :id )",
        ":id ~( ws.epigraph.tests.User :id, ws.epigraph.tests.Person :id )"
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
    testParsingVarProjection(":`record` ( id, bestFriend { deprecated = true } :`record` ( id ) )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":`record` ( friendsMap [ ;param: epigraph.String, doc = \"bla\" ]( :id ) )");
  }

  private void testParsingVarProjection(String str) {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str,
        str
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
        UserId.type,
        UserRecord.type,
        epigraph.String.type
    );

    final StepsAndProjection<OpInputVarProjection> stepsAndProjection = parseOpInputVarProjection(
        varDataType,
        projectionString,
        resolver
    );
    assertEquals(0, stepsAndProjection.pathSteps());
    final @NotNull OpInputVarProjection varProjection = stepsAndProjection.projection();
    String actual = printOpInputVarProjection(varProjection, 0);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  public static StepsAndProjection<OpInputVarProjection> parseOpInputVarProjection(
      @NotNull DataType varDataType,
      @NotNull String projectionString,
      @NotNull TypesResolver resolver) {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    EdlOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        EdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarProjection, errorsAccumulator);

    return runPsiParser(errors -> OpInputProjectionsPsiParser.parseVarProjection(
        varDataType,
        psiVarProjection,
        resolver,
        errors
    ));

  }
}
