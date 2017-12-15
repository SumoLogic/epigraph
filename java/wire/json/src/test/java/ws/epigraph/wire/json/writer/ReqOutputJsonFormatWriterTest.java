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

package ws.epigraph.wire.json.writer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.wire.WireTestUtil.parseOpProjection;
import static ws.epigraph.wire.WireTestUtil.parseReqProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputJsonFormatWriterTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      User2.type,
      UserId2.type,
      UserRecord2.type,
      SubUser.type,
      SubUserId.type,
      SubUserRecord.type,
      String_Person_Map.type,
      PaginationInfo.type,
      PersonMap.type,
      SingleTagEntity.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private final DataType personDataType = new DataType(Person.type, Person.id);
  private final OpProjection<?, ?> personOpProjection = parseOpProjection(personDataType, lines(
      ":(",
      "  id,",
      "  `record` (",
      "    id {",
      "      ;param1 : epigraph.String,",
      "    },",
      "    bestFriend :`record` (",
      "      id,",
      "      bestFriend :`record` (",
      "        id,",
      "        firstName",
      "      ),",
      "    ),",
      "    bestFriend2 $bf2 = :`record` ( id, bestFriend2 $bf2 ),",
      "    bestFriend3 :( id, `record` ( id, firstName, bestFriend3 :`record` ( id, lastName, bestFriend3 : `record` ( id, bestFriend3 $bf3 = :`record` ( id, bestFriend3 $bf3 ) ) ) ) ),",
      "    worstEnemy ( id ) ~ws.epigraph.tests.UserRecord ( profile ),",
      "    friends *( :id ),",
      "    friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) ),",
      "    singleTagField :tag",
      "  )",
      ") :~(",
      "      ws.epigraph.tests.User :`record` (profile)",
      "        :~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
      "      ws.epigraph.tests.User2 :`record` (worstEnemy(id))",
      ")"
  ), resolver);


  @Test
  public void testRenderEmpty() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .toImmutable();

    testRender(":()", person, "{}");
  }

  @Test
  public void testRenderId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .toImmutable();

    testRender(":id", person, "1");
  }

  @Test
  public void testRenderIdParens() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .toImmutable();

    testRender(":(id)", person, "{\"id\":1}");
  }

  @Test
  public void testRenderIdAndRecord() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRender(":(id,record(id))", person, "{\"id\":1,\"record\":{\"id\":1}}");
  }

  @Test
  public void testRenderRecordWithMissingId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRender(":(id,record(id))", person, "{\"id\":null,\"record\":{\"id\":1}}");
  }

  @Test
  public void testRenderRecordWithId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRender(":record(id)", person, "{\"id\":1}");
  }

  @Test
  public void testRenderList() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setFriends(
                        Person_List.create()
                            .add(Person.create().setId(PersonId.create(2)))
                            .add(Person.create().setId(PersonId.create(3)))
                            .add(Person.create().setId(PersonId.create(4)))
                    )
            )
            .toImmutable();

    testRender(":record(friends*(:id))", person, "{\"friends\":[2,3,4]}");
  }

  @Test
  public void testRenderMap() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setFriendsMap(
                        String_Person_Map.create()
                            .put$("key1", Person.create().setId(PersonId.create(1)))
                            .put$("key2", Person.create().setId(PersonId.create(2)))
                            .put$("key3", Person.create().setId(PersonId.create(3)))
                    )
            )
            .toImmutable();

    testRender(
        ":record(friendsMap['key1','key2','key3'](:id))",
        person,
        "{\"friendsMap\":[{\"K\":\"key1\",\"V\":1},{\"K\":\"key2\",\"V\":2},{\"K\":\"key3\",\"V\":3}]}"
    );
  }

  @Test
  public void testRenderTailNoMatch() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                UserRecord.create() // should not match even though this is a UserRecord
                    .setId(PersonId.create(1))
                    .setProfile(Url.create("http://foo"))
            )
            .toImmutable();

    testRender(
        ":record(id):~ws.epigraph.tests.User :record(profile)",
        person,
        "{\"TYPE\":\"ws.epigraph.tests.Person\",\"DATA\":{\"id\":1}}"
    );
  }

  @Test
  public void testRenderSimpleTail() throws IOException {
    final Person.@NotNull Imm person =
        User.create()
            .setId(UserId.create(1))
            .setRecord(
                UserRecord.create()
                    .setId(PersonId.create(1))
                    .setProfile(Url.create("http://foo"))
            )
            .toImmutable();

    testRender(
        ":id:~ws.epigraph.tests.User :record(profile)",
        person,
        "{\"TYPE\":\"ws.epigraph.tests.User\",\"DATA\":{\"id\":1,\"record\":{\"profile\":\"http://foo\"}}}"
    );
  }

  @Test
  public void testRenderSubTail() throws IOException {
    final Person.@NotNull Imm person =
        SubUser.create()
            .setRecord(
                SubUserRecord.create()
                    .setId(PersonId.create(1))
                    .setWorstEnemy(SubUserRecord.create().setId(PersonId.create(1)))
            )
            .toImmutable();

    testRender(
        ":record(id):~ws.epigraph.tests.User :record(profile) :~ws.epigraph.tests.SubUser :record(worstEnemy(id))",
        person,
        "{\"TYPE\":\"ws.epigraph.tests.SubUser\",\"DATA\":{\"id\":1,\"worstEnemy\":{\"id\":1}}}"
    );
  }

  @Test
  public void testRenderModelTail() throws IOException {
    final Person.Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
                    .setWorstEnemy(
                        SubUserRecord.create()
                            .setId(PersonId.create(1))
                            .setProfile(Url.create("foo"))
                    )

            )
            .toImmutable();

    testRender(
        ":record(id,worstEnemy(id)~ws.epigraph.tests.UserRecord(profile))",
        person,
        "{\"id\":1,\"worstEnemy\":{\"TYPE\":\"ws.epigraph.tests.UserRecord\",\"DATA\":{\"id\":1,\"profile\":\"foo\"}}}"
    );
  }

  @Test
  public void testRenderMeta() throws IOException {
    final DataType personMapDataType = new DataType(PersonMap.type, null);
    final OpProjection<?, ?> personMapOpProjection = parseOpProjection(personMapDataType,
        "{ meta: (start, count) } [ required ]( :`record` ( id, firstName ) )", resolver
    );

    String reqProjectionStr = "@(start,count)[ 2 ]:record(id, firstName)";
    final ReqProjection<?, ?> reqProjection =
        parseReqProjection(
            personMapDataType,
            personMapOpProjection,
            reqProjectionStr,
            resolver
        ).projection();

    final PersonMap.Builder personMap = PersonMap.create();
    personMap.put$(
        PersonId.create(2),
        Person.create().setRecord(
            PersonRecord.create().setId(PersonId.create(2)).setFirstName("Alfred")
        )
    );
    personMap.setMeta(PaginationInfo.create()
        .setStart(10L).setCount(20L));

    String expectedJson =
        "{\"META\":{\"start\":10,\"count\":20},\"DATA\":[{\"K\":2,\"V\":{\"id\":2,\"firstName\":\"Alfred\"}}]}";
    testRender(reqProjection, PersonMap.type.createDataBuilder().set(personMap), expectedJson);
  }

  @Test
  public void testRenderRec() throws IOException {
    Person.Builder bf = Person.create();
    bf.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setBestFriend2$(bf)
    );

    Person.Builder person = Person.create()
        .setId(PersonId.create(1))
        .setRecord(
            PersonRecord.create().setId(PersonId.create(1)).setBestFriend2$(bf)
        );

    testRender(
        ":(id,record(id,bestFriend2 $bf= :record(id, bestFriend2 $bf) ))",
        person,
        "{\"id\":1,\"record\":{\"id\":1,\"bestFriend2\":{\"id\":11,\"bestFriend2\":{\"REC\":1}}}}"
    );
  }

  @Test
  public void testRenderRec2() throws IOException {
    Person.Builder bf = Person.create();
    bf.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setFirstName("Alfred")
            .setLastName("Hitchcock")
            .setBestFriend3(bf)
    );

    Person.Builder person = Person.create()
        .setId(PersonId.create(1))
        .setRecord(
            PersonRecord.create().setId(PersonId.create(1)).setBestFriend3(bf)
        );

    testRender(
        ":(id,record(id, " + // parent
        "  bestFriend3 :record( id, firstName, " + // bf-fn
        "    bestFriend3 :record( id, lastName, " + // bf-ln
        "      bestFriend3 $bf= :record(id, bestFriend3 $bf) ))))", //bf-id + bf-rec
        person,
        "{\"id\":1,\"record\":{\"id\":1," + //parent
        "\"bestFriend3\":{\"id\":11,\"firstName\":\"Alfred\"," + // bf-fn
        "\"bestFriend3\":{\"id\":11,\"lastName\":\"Hitchcock\"," + // bf-ln
        "\"bestFriend3\":{\"id\":11," + // bf-id
        "\"bestFriend3\":{\"REC\":1}}}}}}" // bf-red
    );
  }

  @Test
  public void testPolyDataNoProjection() throws IOException {
    Person.Builder pb = Person.create();
    pb.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setFirstName("Alfred")
            .setLastName("Hitchcock")
            .setBestFriend( // Using `User` instead of `Person`
                User.create().setId(UserId.create(1))
            )
            .setWorstEnemy( // Using `UserRecord` instead of `PersonRecord`
                UserRecord.create().setFirstName("Bruce")
            )
    );

    testRender(
        (ReqEntityProjection) null,
        pb,
        "{\"record\":{" +
        "\"id\":11," +
        "\"firstName\":\"Alfred\"," +
        "\"lastName\":\"Hitchcock\"," +
        "\"bestFriend\":{\"TYPE\":\"ws.epigraph.tests.User\",\"DATA\":{\"id\":1}}," +
        "\"worstEnemy\":{\"TYPE\":\"ws.epigraph.tests.UserRecord\",\"DATA\":{\"firstName\":\"Bruce\"}}}}"
    );
  }

  @Test
  public void testPolyListDatumNoProjection() throws IOException {
    Person.Builder pb = Person.create();
    pb.setRecord(
        PersonRecord.create()
            .setFriendRecords(
                PersonRecord_List.create()
                    .add(PersonRecord.create().setFirstName("fn1"))
                    .add(UserRecord.create().setFirstName("fn2"))
            )
    );

    testRender(
        (ReqEntityProjection) null,
        pb,
        "{\"record\":{\"friendRecords\":[" +
        "{\"firstName\":\"fn1\"}," +
        "{\"TYPE\":\"ws.epigraph.tests.UserRecord\",\"DATA\":{\"firstName\":\"fn2\"}}" +
        "]}}"
    );
  }

  @Test
  public void testSingleTagEntity() throws IOException {
    testRender(
        ":record(singleTagField:tag)",
        Person.create()
            .setRecord(PersonRecord.create()
                .setSingleTagField(SingleTagEntity.create().setTag(epigraph.String.create("foo")))),
        "{\"singleTagField\":\"foo\"}"
    );
  }

  private void testRender(@NotNull String reqProjectionStr, @NotNull Data data, @NotNull String expectedJson)
      throws IOException {

    final ReqProjection<?, ?> reqProjection =
        parseReqProjection(personDataType, personOpProjection, reqProjectionStr, resolver).projection();

    testRender(reqProjection, data, expectedJson);

  }

  private void testRender(
      @Nullable ReqProjection<?, ?> reqProjection,
      @NotNull Data data,
      @NotNull String expectedJson)
      throws IOException {

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    final ReqJsonFormatWriter jsonWriter = new ReqJsonFormatWriter(baos);

    if (reqProjection == null)
      jsonWriter.writeData(data.type(), data);
    else
      jsonWriter.writeData(reqProjection, data);


    jsonWriter.close();

    assertEquals(expectedJson, baos.toString());
  }

}
