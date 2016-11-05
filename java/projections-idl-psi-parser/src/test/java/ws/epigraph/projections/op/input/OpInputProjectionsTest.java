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

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpInputVarProjection;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsTest {
  @Test
  public void testParsing1() throws PsiProcessingException {
    String projectionStr = lines(
        ":(",
        "  +id,",
        "  record (",
        "    +id,",
        "    +bestFriend :record (",
        "      +id,",
        "      bestFriend: id { default: 123 }",
        "    ),",
        "    friends { :_ { *( :+id {} ) } }",
        "  )",
        ") ~ws.epigraph.tests.User :record (profile)"
    );

    String expected = lines(
        ":( +id, record ( +id, +bestFriend :record ( +id, bestFriend :id { default: 123 } ), friends *( :+id ) ) )",
        "  ~ws.epigraph.tests.User :record ( profile )"
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
    testParsingVarProjection(":( id, record )");
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
    testParsingVarProjection(":record ( id, firstName )");
  }

  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend :record ( id ) )");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testParsingVarProjection(":record ( id, bestFriend { deprecated = true :record ( id ) } )");
  }

  @Test
  public void testParseList() throws PsiProcessingException {
    testParsingVarProjection(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testParsingVarProjection(":record ( friendsMap []( :id ) )");
  }

  private void testParsingVarProjection(String str) throws PsiProcessingException {
    testParsingVarProjection(
        new DataType(Person.type, Person.id),
        str,
        str
    );
  }

  private void testParsingVarProjection(
      DataType varDataType,
      String projectionString,
      String expected)
      throws PsiProcessingException {

    TypesResolver resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type
    );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpInputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION.rootElementType(),
        IdlOpInputVarProjection.class,
        IdlSubParserDefinitions.OP_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }


    OpInputVarProjection varProjection = null;
    try {
      StepsAndProjection<OpInputVarProjection> stepsAndProjection =
          OpInputProjectionsPsiParser.parseVarProjection(
              varDataType,
              psiVarProjection,
              resolver
          );

      assertEquals(0, stepsAndProjection.pathSteps());
      varProjection = stepsAndProjection.projection();
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + e.location());
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpInputProjectionsPrettyPrinter<NoExceptions> printer = new OpInputProjectionsPrettyPrinter<>(layouter);
    printer.print(varProjection, 0);
    layouter.close();
    String actual = sb.getString();

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
