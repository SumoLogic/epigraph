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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqEntityPath;
import ws.epigraph.url.projections.req.ReqTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private final OpEntityProjection personOpPath = parseOpEntityProjection(
      lines(
          ":`record` { ;p1:epigraph.String }",
          "  / friendsMap { ;p2:epigraph.String }",
          "    / . { ;p3:epigraph.String }",
          "      :`record` { ;p4:epigraph.String }",
          "        / id { ;p5:epigraph.String }"
      )
  );

  @Test
  public void testParsePath() {
    testParse(":record / friendsMap / 'John';p3 = 'foo' :record / id");
  }

  @Test
  public void testParseParam() {
    testParse(":record ;p1 = 'a' / friendsMap ;p2 = 'b' / 'John';p3 = 'c' :record ;p4 = 'd' / id ;p5 = 'e'");
  }

  @Test
  public void testShortPathNotMatching() {
    String expr = ":record / friendsMap / 'John' ;p3 = 'foo' :record";
    UrlReqEntityPath psi = getPsi(expr);

    try {
      TestUtil.runPsiParserNotCatchingErrors(context ->
          ReqPathPsiParser.parseEntityPath(
              personOpPath,
              Person.type.dataType(null),
              psi,
              resolver,
              new ReqPathPsiProcessingContext(context)
          ));
      fail();
    } catch (PsiProcessingException ignored) {
    }
  }

  private void testParse(String expr) {
    testParse(expr, expr);
  }

  private void testParse(String expr, String expectedProjection) {
    UrlReqEntityPath psi = getPsi(expr);

    final @NotNull ReqEntityProjection path = TestUtil.runPsiParser(true, context ->
        ReqPathPsiParser.parseEntityPath(
            personOpPath,
            Person.type.dataType(null),
            psi,
            resolver,
            new ReqPathPsiProcessingContext(context)
        ));

    String s = TestUtil.printReqEntityPath(path);
    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);
  }

  private UrlReqEntityPath getPsi(String projectionString) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqEntityPath psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_ENTITY_PATH,
        errorsAccumulator
    );

    TestUtil.failIfHasErrors(psiVarPath, errorsAccumulator);

    return psiVarPath;
  }

  private @NotNull OpEntityProjection parseOpEntityProjection(String projectionString) {
    return ReqTestUtil.parseOpEntityPath(dataType, projectionString, resolver);
  }

}
