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

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpOutputVarProjection;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.projections.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsParserTest {
  private DataType dataType = new DataType(Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private OpOutputVarProjection personOpProjection = parsePersonOpOutputVarProjection(
      lines(
          ":(",
          "  id,",
          "  record (",
          "    id {",
          "      ;param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
          "    },",
          "    bestFriend :record (",
          "      id,",
          "      bestFriend :record (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    friends *( :id ),",
          "    friendsMap [;keyParam:epigraph.String]( :(id, record (id, firstName) ) )",
          "  )",
          ") ~ws.epigraph.tests.User :record (profile)"
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
  }

  @Test
  public void testParseMapStar() {
    testParse(":record / friendsMap [ * ]( :id )", 3);
  }

  @Test
  public void testParseList() {
    testParse(":record / friends *( :id )", 3);
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
        ":record ( bestFriend :record )",
        1
    );
  }

  @Test
  public void testStarFields() {
    testParse(
        ":record(*)",
        ":record ( id, bestFriend :(), friends *( :() ), friendsMap [ * ]( :() ) )",
        1
    );
  }

  // todo negative test cases too

  private void testParse(String expr, int steps) {
    testParse(expr, expr, steps);
  }

  private void testParse(String expr, String expectedProjection, int steps) {
    UrlReqOutputTrunkVarProjection psi = getPsi(expr);
    List<PsiProcessingError> errors = new ArrayList<>();
    try {

      @NotNull final StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
          ReqOutputProjectionsPsiParser.parseTrunkVarProjection(
              Person.type.dataType(null),
              personOpProjection,
              psi,
              resolver,
              errors
          );

      assertEquals(steps, stepsAndProjection.pathSteps());

      String s = print(stepsAndProjection.projection(), steps);

      final String actual =
          s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
      assertEquals(expectedProjection, actual);

    } catch (PsiProcessingException e) {
//      String psiDump = DebugUtil.psiToString(psi, true, false).trim();
//      System.err.println(psiDump);
//      e.printStackTrace();
//      fail(e.getMessage() + " at " + e.location());
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }

      fail();
    }
  }

  private UrlReqOutputTrunkVarProjection getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION.rootElementType(),
        UrlReqOutputTrunkVarProjection.class,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
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

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    return psiVarProjection;
  }

  private OpOutputVarProjection parsePersonOpOutputVarProjection(String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString);
  }

  private OpOutputVarProjection parseOpOutputVarProjection(DataType varDataType, String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpOutputVarProjection psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION.rootElementType(),
        IdlOpOutputVarProjection.class,
        IdlSubParserDefinitions.OP_OUTPUT_VAR_PROJECTION,
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

    List<PsiProcessingError> errors = new ArrayList<>();
    OpOutputVarProjection varProjection = null;
    try {
      varProjection = OpOutputProjectionsPsiParser.parseVarProjection(
          varDataType,
          psiVarProjection,
          resolver,
          errors
      );

    } catch (PsiProcessingException e) {
//      e.printStackTrace();
//      System.err.println(e.getMessage() + " at " + e.location());
//      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//      fail(psiDump);
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }

      fail();
    }

    return varProjection;
  }

  private String print(ReqOutputVarProjection projection, int pathSteps) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqOutputProjectionsPrettyPrinter<NoExceptions> printer = new ReqOutputProjectionsPrettyPrinter<>(layouter);
    printer.print(projection, pathSteps);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
