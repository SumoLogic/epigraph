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

package ws.epigraph.url.projections.req.input;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqInputVarProjection;
import ws.epigraph.url.projections.req.ReqTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputProjectionsParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private final OpInputVarProjection personOpProjection = parsePersonOpInputVarProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String = \"hello world\" { doc = \"some doc\" }",
          "    },",
          "    bestFriend :(+id, `record` (",
          "      +id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    )),",
          "    friends *( :id ),",
          "    friendsMap []( :(id, `record` (id, firstName) ) )",
          "  ) ~ws.epigraph.tests.UserRecord (profile)",
          ") ~~ws.epigraph.tests.User :`record` (profile)"
      )
  );

  @Test
  public void testParseIdTag() {
    testParse(":id");
  }

  @Test
  public void testParseRecordTag() {
    testParse(":record");
  }

  @Test
  public void testParseMultiTag() {
    testParse(":(id,record)", ":( id, record )");
  }

  @Test
  public void testParseRecord() {
    testParse(":record ( id ;param1 = 'foo', friends *( :id ) )");
  }

  @Test
  public void testParseMap() {
    testParse(":record ( friendsMap [ '1', '2', '3' ]( :id ) )");
  }

  @Test
  public void testParseMapEmptyKeys() {
    testParse(":record ( friendsMap []( :id ) )");
  }

  @Test
  public void testParseList() {
    testParse(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseTail() {
    testParse(
        ":id ~~User :record ( profile )",
        ":id ~~ws.epigraph.tests.User :record ( profile )"
    );
  }

  @Test
  public void testParseModelTail() {
    testParse(
        ":record (id) ~UserRecord (profile)",
        ":record ( id ) ~ws.epigraph.tests.UserRecord ( profile )"
    );
  }

  // negative cases

  @Test
  public void testRequiredTag() {
    testParseFail(":record(bestFriend:record(id))");
  }

  @Test
  public void testRequiredField() {
    testParseFail(":record(bestFriend:(id,record()))");
  }

  @Test
  public void testRequiredPresent() {
    testParse(":record ( bestFriend :( id, record ( id ) ) )");
  }

  private void testParse(String expr) {
    testParse(expr, expr);
  }

  private void testParseFail(String expr) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqInputVarProjection psi = EpigraphPsiUtil.parseText(
        expr,
        UrlSubParserDefinitions.REQ_INPUT_VAR_PROJECTION,
        errorsAccumulator
    );

    failIfHasErrors(psi, errorsAccumulator);

    try {
      TestUtil.runPsiParserNotCatchingErrors(context -> {
        ReqInputVarReferenceContext reqInputVarReferenceContext = new ReqInputVarReferenceContext(Qn.EMPTY, null);

        ReqInputPsiProcessingContext reqInputPsiProcessingContext =
            new ReqInputPsiProcessingContext(context, reqInputVarReferenceContext);

        ReqInputVarProjection vp = ReqInputProjectionsPsiParser.parseVarProjection(
            dataType,
            personOpProjection,
            psi,
            resolver,
            reqInputPsiProcessingContext
        );

        reqInputVarReferenceContext.ensureAllReferencesResolved(context);

        return vp;
      });
      fail();
    } catch (PsiProcessingException ignored) {
    }
  }

  private void testParse(String expr, String expectedProjection) {
    final @NotNull ReqInputVarProjection varProjection =
        ReqTestUtil.parseReqInputVarProjection(dataType, personOpProjection, expr, resolver);

    String s = TestUtil.printReqInputVarProjection(varProjection);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);
  }

  private @NotNull OpInputVarProjection parsePersonOpInputVarProjection(@NotNull String projectionString) {
    return ReqTestUtil.parseOpInputVarProjection(dataType, projectionString, resolver);
  }
}
