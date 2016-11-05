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

package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpVarPath;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.path.ReqPathPrettyPrinter;
import ws.epigraph.projections.req.path.ReqVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.projections.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqVarPath;
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
public class ReqPathParserTest {
  private DataType dataType = new DataType(Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private OpVarPath personOpPath = parseOpVarPath(
      lines(
          ":record { ;p1:epigraph.String }",
          "  / friendsMap { ;p2:epigraph.String }",
          "    / . { ;p3:epigraph.String }",
          "      :record { ;p4:epigraph.String }",
          "        / id { ;p5:epigraph.String }"
      )
  );

  @Test
  public void testParsePath() {
    testParse(":record / friendsMap / 'John' ;p3 = 'foo' :record / id");
  }

  @Test
  public void testParseParam() {
    testParse(":record ;p1 = 'a' / friendsMap ;p2 = 'b' / 'John' ;p3 = 'c' :record ;p4 = 'd' / id ;p5 = 'e'");
  }
  private void testParse(String expr) {
    testParse(expr, expr);
  }

  private void testParse(String expr, String expectedProjection) {
    UrlReqVarPath psi = getPsi(expr);
    List<PsiProcessingError> errors = new ArrayList<>();
    try {
      final ReqVarPath reqVarPath =
          ReqPathPsiParser.parseVarPath(personOpPath, Person.type.dataType(null), psi, resolver, errors);

      String s = print(reqVarPath);

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

  private UrlReqVarPath getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqVarPath psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_VAR_PATH.rootElementType(),
        UrlReqVarPath.class,
        UrlSubParserDefinitions.REQ_VAR_PATH,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psiVarPath, true, false).trim();
      fail(psiDump);
    }

//    String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//    System.out.println(psiDump);

    return psiVarPath;
  }

  private OpVarPath parseOpVarPath(String projectionString) {
    return parseOpVarPath(dataType, projectionString);
  }

  private OpVarPath parseOpVarPath(DataType varDataType, String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH.rootElementType(),
        IdlOpVarPath.class,
        IdlSubParserDefinitions.OP_VAR_PATH,
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

    OpVarPath varPath = null;
    try {
      varPath = OpPathPsiParser.parseVarPath(varDataType, psiVarProjection, resolver);

    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + e.location());
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    return varPath;
  }

  private String print(ReqVarPath path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    ReqPathPrettyPrinter<NoExceptions> printer = new ReqPathPrettyPrinter<>(layouter);
    int len = ProjectionUtils.pathLength(path);
    printer.print(path, len);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }
}
