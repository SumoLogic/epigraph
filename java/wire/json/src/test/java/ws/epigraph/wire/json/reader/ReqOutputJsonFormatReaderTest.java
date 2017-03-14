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

package ws.epigraph.wire.json.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.data.DataComparator;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.wire.json.writer.JsonFormatWriter;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.lines;
import static ws.epigraph.wire.WireTestUtil.parseOpOutputVarProjection;
import static ws.epigraph.wire.WireTestUtil.parseReqOutputVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputJsonFormatReaderTest {
  private final DataType dataType = new DataType(Person.type, Person.id);
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

  private final OpOutputVarProjection personOpProjection = parsePersonOpOutputVarProjection(
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
      )
  );

  @Test
  public void testReadEmpty() throws IOException {
    final Person.@NotNull Imm person = Person.create().toImmutable();
    testRead(":()", "{}", person);
  }

  @Test
  public void testReadId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .toImmutable();

    testRead(":id", "1", person);
  }

  @Test
  public void testReadIdParens() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .toImmutable();

    testRead(":(id)", "{\"id\":1}", person);
  }

  @Test
  public void testReadIdAndRecord() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setId(PersonId.create(1))
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRead(":(id,record(id))", "{\"id\":1,\"record\":{\"id\":1}}", person);
  }

  @Test
  public void testReadRecordWithMissingId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRead(":(id,record(id))", "{\"record\":{\"id\":1}}", person);
  }

  @Test
  public void testReadRecordWithId() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
            )
            .toImmutable();

    testRead(":record(id)", "{\"id\":1}", person);
  }

  @Test
  public void testReadList() throws IOException {
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

    testRead(":record(friends*(:id))", "{\"friends\":[2,3,4]}", person);
  }

  @Test
  public void testReadMap() throws IOException {
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

    testRead(
        ":record(friendsMap['key1','key2','key3'](:id))",
        "{\"friendsMap\":[{\"K\":\"key1\",\"V\":1},{\"K\":\"key2\",\"V\":2},{\"K\":\"key3\",\"V\":3}]}", person
    );
  }

  @Test
  public void testReadTailNoMatch() throws IOException {
    testReadFail(
        ":record(id)~~ws.epigraph.tests.User :record(profile)",
        "{\"type\":\"ws.epigraph.tests.Person\",\"data\":{\"id\":1,\"record\":{\"profile\":\"http://foo\"}}}",
        "Unknown field 'record' in record type 'ws.epigraph.tests.PersonRecord'"
    );
  }

  @Test
  public void testReadSimpleTail() throws IOException {
    final Person.@NotNull Imm person =
        User.create()
            .setId(UserId.create(1))
            .setRecord(
                UserRecord.create()
                    .setProfile(Url.create("http://foo"))
            )
            .toImmutable();

    testRead(
        ":id~~ws.epigraph.tests.User :record(profile)",
        "{\"type\":\"ws.epigraph.tests.User\",\"data\":{\"id\":1,\"record\":{\"profile\":\"http://foo\"}}}", person
    );
  }

  @Test
  public void testReadSubTail() throws IOException {
    final Person.@NotNull Imm person =
        SubUser.create()
            .setRecord(
                SubUserRecord.create()
                    .setId(PersonId.create(1))
                    .setWorstEnemy(SubUserRecord.create().setId(PersonId.create(1)))
            )
            .toImmutable();

    testRead(
        ":record(id)~~ws.epigraph.tests.User :record(profile) ~~ws.epigraph.tests.SubUser :record(worstEnemy(id))",
        "{\"type\":\"ws.epigraph.tests.SubUser\",\"data\":{\"id\":1,\"worstEnemy\":{\"id\":1}}}", person
    );
  }

  @Test
  public void testReadModelTail() throws IOException {
    final Person.@NotNull Imm person =
        Person.create()
            .setRecord(
                PersonRecord.create()
                    .setId(PersonId.create(1))
                    .setWorstEnemy(
                        UserRecord.create()
                            .setId(PersonId.create(1))
                            .setProfile(Url.create("foo"))
                    )

            )
            .toImmutable();

    testRead(
        ":record(id,worstEnemy(id)~ws.epigraph.tests.UserRecord(profile))",
        "{\"id\":1,\"worstEnemy\":{\"type\":\"ws.epigraph.tests.UserRecord\",\"data\":{\"id\":1,\"profile\":\"foo\"}}}",
        person
    );
  }

  @Test
  public void testReadMeta() throws IOException {
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

    String json =
        "{\"meta\":{\"start\":10,\"count\":20},\"data\":[{\"K\":2,\"V\":{\"id\":2,\"firstName\":\"Alfred\"}}]}";

    testRead(
        reqProjection,
        json,
        PersonMap.type.createDataBuilder().set(personMap)
    );
  }

  @Test
  public void testReadRec() throws IOException {
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

    testRead(
        ":(id,record(id,bestFriend2 $bf= :record(id, bestFriend2 $bf) ))",
        "{\"id\":1,\"record\":{\"id\":1,\"bestFriend2\":{\"id\":11,\"bestFriend2\":{\"REC\":1}}}}",
        person
    );
  }

  @Test
  public void testReadRec2() throws IOException {
    Person.Builder bfrec = Person.create();
    bfrec.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setBestFriend3$(bfrec)
    );

    Person.Builder bf = Person.create();
    bf.setRecord(
        PersonRecord.create()
            .setId(PersonId.create(11))
            .setFirstName("Alfred")
            .setBestFriend3$(
                Person.create().setRecord(
                    PersonRecord.create()
                        .setId(PersonId.create(11))
                        .setLastName("Hitchcock")
                        .setBestFriend3$(bfrec)
                )
            )
    );

    Person.Builder person = Person.create()
        .setId(PersonId.create(1))
        .setRecord(
            PersonRecord.create().setId(PersonId.create(1)).setBestFriend3$(bf)
        );

    testRead(
        ":(id,record(id, " +
        "bestFriend3 :record( id, firstName, " +
        "bestFriend3 :record( id, lastName, " +
        "bestFriend3 $bf= :record(id, bestFriend3 $bf) ))))",

        "{\"id\":1,\"record\":{\"id\":1," +
        "\"bestFriend3\":{\"id\":11,\"firstName\":\"Alfred\"," +
        "\"bestFriend3\":{\"id\":11,\"lastName\":\"Hitchcock\"," +
        "\"bestFriend3\":{\"id\":11," +
        "\"bestFriend3\":{\"REC\":1}}}}}}",
        person
    );
  }

  private void testRead(
      @NotNull String reqProjectionStr,
      @NotNull String json,
      @NotNull Data expectedData)
      throws IOException {

    final @NotNull ReqOutputVarProjection reqProjection =
        parseReqOutputVarProjection(dataType, personOpProjection, reqProjectionStr, resolver).projection();

    testRead(reqProjection, json, expectedData);
  }

  private void testRead(
      @NotNull ReqOutputVarProjection reqProjection,
      @NotNull String json,
      @NotNull Data expectedData)
      throws IOException {

    JsonParser parser = new JsonFactory().createParser(json);
    ReqOutputJsonFormatReader jsonReader = new ReqOutputJsonFormatReader(parser);

    final Data data = jsonReader.readData(reqProjection);

    if (!new DataComparator().equals(expectedData, data)) {
      StringWriter writer = new StringWriter();
      JsonFormatWriter jsonWriter = new JsonFormatWriter(writer);
      jsonWriter.writeData(data);
      String dataStr = writer.toString();

      writer = new StringWriter();
      jsonWriter = new JsonFormatWriter(writer);
      jsonWriter.writeData(expectedData);
      String expectedDataStr = writer.toString();

      fail("\nexpected:\n" + expectedDataStr + "\nactual:\n" + dataStr);
    }
  }

  private void testReadFail(
      @NotNull String reqProjectionStr,
      @NotNull String json,
      @Nullable String errorMessageSubstring) throws IOException {

    final @NotNull ReqOutputVarProjection reqProjection =
        parseReqOutputVarProjection(dataType, personOpProjection, reqProjectionStr, resolver).projection();

    JsonParser parser = new JsonFactory().createParser(json);
    ReqOutputJsonFormatReader jsonReader = new ReqOutputJsonFormatReader(parser);

    try {
      jsonReader.readData(reqProjection);
      fail();
    } catch (IllegalArgumentException e) {
      if (errorMessageSubstring != null)
        assertTrue(e.getMessage().contains(errorMessageSubstring));
    }
  }

  private @NotNull OpOutputVarProjection parsePersonOpOutputVarProjection(@NotNull String projectionString) {
    return parseOpOutputVarProjection(dataType, projectionString, resolver);
  }
}
