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

package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.projections.req.path.ReqVarPath;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqOutputTrunkVarProjection;
import ws.epigraph.url.projections.req.ReqTestUtil;

import static org.junit.Assert.*;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private final OpVarPath personOpPath = parseOpVarPath(
      lines(
          ":`record` { ;p1:epigraph.String }",
          "  / friendsMap { ;p2:epigraph.String }",
          "    / . { ;p3:epigraph.String }",
          "      :`record` { ;p4:epigraph.String }",
          "        / bestFriend"
      )
  );

  @Test
  public void testParsePath() {
    testParse(personOpPath, ":record / friendsMap / 'John';p3 = 'foo' :record / bestFriend");
  }

  @Test
  public void testParseParam() {
    testParse(
        personOpPath,
        ":record ;p1 = 'a' / friendsMap ;p2 = 'b' / 'John';p3 = 'c' :record ;p4 = 'd' / bestFriend"
    );
  }

  @Test
  public void testComaFail1() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John' :record ( bestFriend )");
  }

  @Test
  public void testComaFail2() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John' :(record ( bestFriend ))");
  }

  @Test
  public void testComaFail3() {
    testPathNotMatched(personOpPath, ":record / friendsMap");
  }

  @Test
  public void testComaFail4() {
    testPathNotMatched(personOpPath, ":record / friendsMap / 'John'");
  }

  @Test
  public void testComaFail5() {
    testPathNotMatched(personOpPath, "");
  }

  @Test
  public void testComa1() {
    testParse(
        personOpPath,
        ":record / friendsMap / 'John' :record / bestFriend :record (id)",
        ":record / friendsMap / 'John' :record / bestFriend",
        ":record (id)"
    );
  }

  @Test
  public void testComa2() {
    testParse(
        personOpPath,
        ":record / friendsMap / 'John' :record / bestFriend :(record (id))",
        ":record / friendsMap / 'John' :record / bestFriend",
        ":(record (id))"
    );
  }

  private void testParse(OpVarPath opPath, String expr) {
    testParse(opPath, expr, expr, null);
  }

  private void testPathNotMatched(OpVarPath opPath, String expr) {
    try {
      UrlReqOutputTrunkVarProjection psi = getPsi(expr);
      PsiProcessingContext psiProcessingContext = new DefaultPsiProcessingContext();
      ReqPathPsiProcessingContext pathPsiProcessingContext = new ReqPathPsiProcessingContext(psiProcessingContext);
      ReadReqPathPsiParser.parseVarPath(opPath, Person.type.dataType(null), psi, resolver, pathPsiProcessingContext);

      fail("Expected to get 'path not matched' error");
    } catch (PathNotMatchedException ignored) {
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      fail(e.getMessage() + " at " + e.location());
    }
  }

  private void testParse(OpVarPath opPath, String expr, String expectedPath, @Nullable String expectedPsiRemainder) {

    UrlReqOutputTrunkVarProjection psi = getPsi(expr);
    final ReadReqPathParsingResult<ReqVarPath> result =
        TestUtil.runPsiParser(true, context -> ReadReqPathPsiParser.parseVarPath(
            opPath,
            Person.type.dataType(null),
            psi,
            resolver,
            new ReqPathPsiProcessingContext(context)
        ));

    String s = TestUtil.printReqVarPath(result.path());

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedPath, actual);

    final PsiElement trunkProjectionPsi = result.trunkProjectionPsi();
    final PsiElement comaProjectionPsi = result.comaProjectionPsi();

    if (expectedPsiRemainder == null) {
      if (trunkProjectionPsi != null) {
        // should be empty
        assertEquals("", trunkProjectionPsi.getText());
      }

      if (comaProjectionPsi != null) {
        assertEquals("", comaProjectionPsi.getText());
      }

    } else {
      PsiElement remPsi = trunkProjectionPsi;
      if (remPsi == null) remPsi = comaProjectionPsi;
      assertNotNull(remPsi);

      assertEquals(expectedPsiRemainder, remPsi.getText());
    }

  }

  private UrlReqOutputTrunkVarProjection getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqOutputTrunkVarProjection psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_OUTPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psiVarPath, errorsAccumulator);

    return psiVarPath;
  }

  private OpVarPath parseOpVarPath(String projectionString) {
    return ReqTestUtil.parseOpVarPath(dataType, projectionString, resolver);
  }

}
