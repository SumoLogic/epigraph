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

package ws.epigraph.url.projections.req.delete;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.projections.req.delete.ReqDeleteVarProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.projections.req.ReqTestUtil;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteProjectionsParserTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type
  );

  private final OpDeleteVarProjection personOpProjection = parsePersonOpDeleteVarProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id {",
          "      ;param1 : epigraph.String = \"hello world\" { doc = \"some doc\" },",
          "    },",
          "    bestFriend :`record` (",
          "      id,",
          "      bestFriend :`record` (",
          "        id,",
          "        firstName",
          "      ),",
          "    ),",
          "    friends *( :id ),",
          "    friendsMap []( :(id, `record` (id, firstName) ) )",
          "  )",
          ") ~ws.epigraph.tests.User :`record` (profile)"
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
    testParse(":record ( id ;param1 = 'foo' )");
  }

  @Test
  public void testParseMap() {
    testParse(":record ( friendsMap [ '1', '2', '3' ]( :id ) )");
  }

  @Test
  public void testParseList() {
    testParse(":record ( friends *( :id ) )");
  }

  @Test
  public void testParseTail() {
    testParse(
        ":id ~User :record ( profile )",
        ":id ~ws.epigraph.tests.User :record ( profile )"
    );
  }


  private void testParse(String expr) {
    testParse(expr, expr);
  }

  private void testParse(String expr, String expectedProjection) {
    final @NotNull ReqDeleteVarProjection varProjection =
        ReqTestUtil.parseReqDeleteVarProjection(dataType, personOpProjection, expr, resolver);

    String s = TestUtil.printReqDeleteVarProjection(varProjection);

    final String actual =
        s.replaceAll("\"", "'"); // pretty printer outputs double quotes, we use single quotes in URLs
    assertEquals(expectedProjection, actual);
  }

  private @NotNull OpDeleteVarProjection parsePersonOpDeleteVarProjection(@NotNull String projectionString) {
    return ReqTestUtil.parseOpDeleteVarProjection(dataType, projectionString, resolver);
  }
}
