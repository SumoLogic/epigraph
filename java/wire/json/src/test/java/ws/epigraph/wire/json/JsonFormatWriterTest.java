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

package ws.epigraph.wire.json;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.wire.WireTestUtil.parseOpOutputVarProjection;
import static ws.epigraph.wire.WireTestUtil.parseReqOutputVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class JsonFormatWriterTest {
  private DataType dataType = new DataType(Person.type, Person.id);
  private TypesResolver resolver = new SimpleTypesResolver(
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
      epigraph.String.type,
      epigraph.Boolean.type
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
          ") ~(",
          "      ws.epigraph.tests.User :record (profile)",
          "        ~ws.epigraph.tests.SubUser :record (worstEnemy(id)),",
          "      ws.epigraph.tests.User2 :record (worstEnemy(id))",
          ")"
      )
  );

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
        ":record(id)~ws.epigraph.tests.User :record(profile)",
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
        ":id~ws.epigraph.tests.User :record(profile)",
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
        ":record(id)~ws.epigraph.tests.User :record(profile) ~ws.epigraph.tests.SubUser :record(worstEnemy(id))",
        person,
        "{\"type\":\"ws.epigraph.tests.SubUser\",\"data\":{\"id\":1,\"worstEnemy\":{\"id\":1}}}"
    );
  }

  private void testRender(@NotNull String reqProjectionStr, @NotNull Data data, @NotNull String expectedJson)
      throws IOException {
    final @NotNull ReqOutputVarProjection reqProjection =
        parseReqOutputVarProjection(dataType, personOpProjection, reqProjectionStr, resolver).projection();

    final StringWriter writer = new StringWriter();
    final JsonFormatWriter jsonWriter = new JsonFormatWriter(writer);

    jsonWriter.writeData(reqProjection, data);

    assertEquals(expectedJson, writer.toString());
  }

  @NotNull
  private OpOutputVarProjection parsePersonOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString, resolver);
  }
}
