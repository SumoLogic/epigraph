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
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.wire.WireTestUtil.parseOpOutputVarProjection;
import static ws.epigraph.wire.WireTestUtil.parseReqOutputVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class JsonFormatWriterTest {
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
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private final DataType personDataType = new DataType(Person.type, Person.id);
  private final OpOutputVarProjection personOpProjection = parseOpOutputVarProjection(personDataType, lines(
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
      "    friendsMap [;keyParam:epigraph.String]( :(id, `record` (id, firstName) ) )",
      "  )",
      ") ~~(",
      "      ws.epigraph.tests.User :`record` (profile)",
      "        ~~ws.epigraph.tests.SubUser :`record` (worstEnemy(id)),",
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
                            .put$(
                                epigraph.String.create("key1"),
                                Person.create().setId(PersonId.create(1))
                            )
                            .put$(
                                epigraph.String.create("key2"),
                                Person.create().setId(PersonId.create(2))
                            )
                            .put$(
                                epigraph.String.create("key3"),
                                Person.create().setId(PersonId.create(3))
                            )
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
        ":record(id)~~ws.epigraph.tests.User :record(profile)",
        person,
        "{\"type\":\"ws.epigraph.tests.Person\",\"data\":{\"id\":1}}"
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
        ":id~~ws.epigraph.tests.User :record(profile)",
        person,
        "{\"type\":\"ws.epigraph.tests.User\",\"data\":{\"id\":1,\"record\":{\"profile\":\"http://foo\"}}}"
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
        ":record(id)~~ws.epigraph.tests.User :record(profile) ~~ws.epigraph.tests.SubUser :record(worstEnemy(id))",
        person,
        "{\"type\":\"ws.epigraph.tests.SubUser\",\"data\":{\"id\":1,\"worstEnemy\":{\"id\":1}}}"
    );
  }

  @Test
  public void testRenderModelTail() throws IOException {
    final Person.@NotNull Imm person =
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
        "{\"id\":1,\"worstEnemy\":{\"type\":\"ws.epigraph.tests.UserRecord\",\"data\":{\"id\":1,\"profile\":\"foo\"}}}"
    );
  }

  @Test
  public void testRenderMeta() throws IOException {
    final DataType personMapDataType = new DataType(PersonMap.type, null);
    final OpOutputVarProjection personMapOpProjection = parseOpOutputVarProjection(personMapDataType,
        "{ meta: (start, count) } [ required ]( :`record` ( id, firstName ) )", resolver
    );

    String reqProjectionStr = "[ 2 ](:record(id, firstName))@(start,count)";
    final @NotNull ReqOutputVarProjection reqProjection =
        parseReqOutputVarProjection(personMapDataType, personMapOpProjection, reqProjectionStr, resolver).projection();

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
        "{\"meta\":{\"start\":10,\"count\":20},\"data\":[{\"K\":2,\"V\":{\"id\":2,\"firstName\":\"Alfred\"}}]}";
    testRender(reqProjection, PersonMap.type.createDataBuilder().set(personMap), expectedJson);
  }

  @Test
  public void testRenderRec() throws IOException {
    Person.Builder bf = Person.create();
    bf.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setBestFriend2(bf)
    );

    Person.Builder person = Person.create()
        .setId(PersonId.create(1))
        .setRecord(
            PersonRecord.create().setId(PersonId.create(1)).setBestFriend2(bf)
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

  private void testRender(@NotNull String reqProjectionStr, @NotNull Data data, @NotNull String expectedJson)
      throws IOException {
    final @NotNull ReqOutputVarProjection reqProjection =
        parseReqOutputVarProjection(personDataType, personOpProjection, reqProjectionStr, resolver).projection();

    testRender(reqProjection, data, expectedJson);
  }

  private void testRender(
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull Data data,
      @NotNull String expectedJson)
      throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final JsonFormatWriter jsonWriter = new JsonFormatWriter(baos);

    jsonWriter.writeData(reqProjection, data);
    jsonWriter.close();

    assertEquals(expectedJson, baos.toString());
  }

}
