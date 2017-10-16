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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumTypeApi;

import java.util.Collections;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DefaultReqProjectionConstructorTest {
  private final DataType dataType = new DataType(Person.type, null);
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      epigraph.String.type,
      epigraph.Integer.type
  );

  private final OpEntityProjection personOpProjection = parsePersonOpOutputEntityProjection(
      lines(
          ":(",
          "  id,",
          "  `record` (",
          "    id,",
          "    bestFriend :(",
          "      +id { ;param1: epigraph.Integer },",
          "      `record` (",
          "        +id { ;param2: epigraph.Integer { default: 333 } },",
          "        bestFriend :`record` (",
          "          id,",
          "          firstName",
          "        ),",
          "      ),",
          "    ),",
          "    bestFriend2 $bf2 = :`record` ( +id, bestFriend2 $bf2 ),",
          "    bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( +id, bestFriend3 $bf3 ) ) ) ) ),",
          "    friends *( :+id ),",
          "    friendsMap []( :(id, `record` (id, +firstName) ) )",
          "    friendsMap2 { meta: (+start, count) } [] (:id)",
          "  ) ~ws.epigraph.tests.UserRecord (profile)",
          ") :~ws.epigraph.tests.User :`record` (profile)"
      )
  );

  @Test
  public void testIncludeNone() throws PsiProcessingException {
    test(dataType, personOpProjection, DefaultReqProjectionConstructor.Mode.INCLUDE_NONE, ":()");
  }

  @Test
  public void testIncludeFlaggedOnlyPrimitive() throws PsiProcessingException {
    DataType dataType = PersonId.type.dataType();
    ReqEntityProjection req = test(
        dataType,
        ReqTestUtil.parseOpOutputEntityProjection(dataType, "", resolver),
        DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        ""
    );

    assertEquals(1, req.tagProjections().size());
    ReqTagProjectionEntry monoTpe = req.tagProjection(DatumTypeApi.MONO_TAG_NAME);
    assertNotNull(monoTpe);
    ReqModelProjection<?, ?, ?> modelProjection = monoTpe.projection();
    assertTrue(modelProjection instanceof ReqPrimitiveModelProjection);
  }

  @Test
  public void testIncludeNonePrimitiveList() throws PsiProcessingException {
    DataType dataType = PersonId_List.type.dataType();
    ReqEntityProjection req = test(
        dataType,
        ReqTestUtil.parseOpOutputEntityProjection(dataType, "", resolver),
        DefaultReqProjectionConstructor.Mode.INCLUDE_NONE,
        "" // * :id
    );

    assertEquals(1, req.tagProjections().size());
    ReqTagProjectionEntry monoTpe = req.tagProjection(DatumTypeApi.MONO_TAG_NAME);
    assertNotNull(monoTpe);
    ReqModelProjection<?, ?, ?> modelProjection = monoTpe.projection();
    assertTrue(modelProjection instanceof ReqListModelProjection);
    ReqListModelProjection listModelProjection = (ReqListModelProjection) modelProjection;
    ReqEntityProjection itemsProjection = listModelProjection.itemsProjection();
    assertEquals(1, itemsProjection.tagProjections().size());
    assertNotNull(itemsProjection.tagProjection(DatumTypeApi.MONO_TAG_NAME));
  }

  @Test
  public void testIncludeFlaggedOnlyPrimitiveList() throws PsiProcessingException {
    DataType dataType = PersonId_List.type.dataType();
    ReqEntityProjection req = test(
        dataType,
        ReqTestUtil.parseOpOutputEntityProjection(dataType, "", resolver),
        DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        "" // * :id
    );

    assertEquals(1, req.tagProjections().size());
    ReqTagProjectionEntry monoTpe = req.tagProjection(DatumTypeApi.MONO_TAG_NAME);
    assertNotNull(monoTpe);
    ReqModelProjection<?, ?, ?> modelProjection = monoTpe.projection();
    assertTrue(modelProjection instanceof ReqListModelProjection);
    ReqListModelProjection listModelProjection = (ReqListModelProjection) modelProjection;
    ReqEntityProjection itemsProjection = listModelProjection.itemsProjection();
    assertEquals(1, itemsProjection.tagProjections().size());
    assertNotNull(itemsProjection.tagProjection(DatumTypeApi.MONO_TAG_NAME));
  }

  @Test
  public void testRetro() throws PsiProcessingException {
    test(
        dataType,
        parsePersonOpOutputEntityProjection(":`record`(+bestFriend2)"),
        DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        ":record ( bestFriend2 :id )"
    );
  }

  @Test
  public void testMeta() throws PsiProcessingException {
    OpEntityProjection op = parsePersonOpOutputEntityProjection(":`record`(friendsMap2 {meta:(+start,count)}[](:+id))");

    test(
        dataType,
        op,
        DefaultReqProjectionConstructor.Mode.INCLUDE_NONE,
        ":()"
    );

    test(
        dataType,
        op,
        DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        ":record ( friendsMap2 @( start ) [ * ]( :id ) )"
    );

    test(
        dataType,
        op,
        DefaultReqProjectionConstructor.Mode.INCLUDE_ALL,
        ":record ( friendsMap2 @( start, count ) [ * ]( :id ) )"
    );
  }

  @Test
  public void testIncludeFlaggedOnly() throws PsiProcessingException {
    test(dataType, personOpProjection, DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        lines(
            ":record (",
            "  bestFriend :( id, record ( id ;param2 = 333 ) ),",
            "  bestFriend2 $bf2 = :record ( id, bestFriend2 $bf2 ),",
            "  bestFriend3",
            "    :record ( bestFriend3 :record ( bestFriend3 :record ( bestFriend3 $bf3 = :record ( id, bestFriend3 $bf3 ) ) ) ),",
            "  friends *( :id ),",
            "  friendsMap [ * ]( :record ( firstName ) )",
            ")"
        )
    );
  }

  @Test
  public void testIncludeAll() throws PsiProcessingException {
    test(dataType, personOpProjection, DefaultReqProjectionConstructor.Mode.INCLUDE_ALL,
        lines(
            ":(",
            "  id,",
            "  record (",
            "    id,",
            "    bestFriend :( id, record ( id ;param2 = 333, bestFriend :record ( id, firstName ) ) ),",
            "    bestFriend2 $bf2 = :record ( id, bestFriend2 $bf2 ),",
            "    bestFriend3",
            "      :(",
            "        id,",
            "        record (",
            "          id,",
            "          firstName,",
            "          bestFriend3",
            "            :record ( id, lastName, bestFriend3 :record ( id, bestFriend3 $bf3 = :record ( id, bestFriend3 $bf3 ) ) )",
            "        )",
            "      ),",
            "    friends *( :id ),",
            "    friendsMap [ * ]( :( id, record ( id, firstName ) ) ),",
            "    friendsMap2 @( start, count ) [ * ]( :id )",
            "  ) ~ws.epigraph.tests.UserRecord ( profile )",
            ") :~ws.epigraph.tests.User :record ( profile )"
        )
    );
  }

  @Test
  public void testWithData() throws PsiProcessingException {
    test(
        dataType,
        parsePersonOpOutputEntityProjection(":`record`(friendsMap[required]:id)"),
        DefaultReqProjectionConstructor.Mode.INCLUDE_ALL,
        Person.create().setRecord(
            PersonRecord.create().setFriendsMap(
                String_Person_Map.create()
                    .put$("a", Person.create().setId(PersonId.create(1)))
                    .put$("b", Person.create().setId(PersonId.create(2)))
            )
        ),
        ":record ( friendsMap [ 'a', 'b' ]( :id ) )"
    );
  }

  @Test
  public void testWithKeyParams() throws PsiProcessingException {
    test(
        dataType,
        parsePersonOpOutputEntityProjection(
            ":`record`(friendsMap[required,;p:epigraph.Integer {default: 555} ]:id)"
        ),
        DefaultReqProjectionConstructor.Mode.INCLUDE_ALL,
        Person.create().setRecord(
            PersonRecord.create().setFriendsMap(
                String_Person_Map.create()
                    .put$("a", Person.create().setId(PersonId.create(1)))
                    .put$("b", Person.create().setId(PersonId.create(2)))
            )
        ),
        ":record ( friendsMap [ 'a';p = 555, 'b';p = 555 ]( :id ) )"
    );
  }

  @Test
  public void testSingleTagNonParenthesized() throws PsiProcessingException {
    ReqEntityProjection req = test(
        dataType,
        parsePersonOpOutputEntityProjection(":( id, `record`(+id) )"),
        DefaultReqProjectionConstructor.Mode.INCLUDE_FLAGGED_ONLY,
        ":record ( id )"
    );
    assertFalse(req.parenthesized());
  }

  private ReqEntityProjection test(
      DataType type,
      OpEntityProjection op,
      DefaultReqProjectionConstructor.Mode mode,
      String expectedReq)
      throws PsiProcessingException {

    return test(type, op, mode, null, expectedReq);
  }

  private ReqEntityProjection test(
      DataType type,
      OpEntityProjection op,
      DefaultReqProjectionConstructor.Mode mode,
      Data data,
      String expectedReq)
      throws PsiProcessingException {

    PsiProcessingContext ppc = new DefaultPsiProcessingContext();
    ReqEntityProjection req = new DefaultReqProjectionConstructor(mode, true, false).createDefaultEntityProjection(
        type,
        op,
        false,
        data == null ? null : Collections.singletonList(data),
        resolver,
        TextLocation.UNKNOWN,
        ppc
    );

    TestUtil.failIfHasErrors(true, ppc.messages());

    assertEquals(
        expectedReq,
        TestUtil.printReqEntityProjection(req, 0)
    );

    return req;
  }

  private @NotNull OpEntityProjection parsePersonOpOutputEntityProjection(@NotNull String projectionString) {
    return ReqTestUtil.parseOpOutputEntityProjection(dataType, projectionString, resolver);
  }

}
