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

package ws.epigraph.client;

import epigraph.Error;
import epigraph.PersonId_Error_Map;
import junit.framework.TestCase;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.*;
import ws.epigraph.tests.resources.users.client.UsersClient;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractGeneratedClientTest {
  protected static final int PORT = 8888;
  protected static final String HOST = "localhost";
  protected static final int TIMEOUT = 100; // ms

  protected static CloseableHttpAsyncClient httpClient;
  protected static UsersClient client;

  @BeforeClass
  public static void startClient() {
    httpClient = HttpAsyncClients.createDefault();
    httpClient.start();

    client = new UsersClient(new HttpHost(HOST, PORT), httpClient);
  }

  @AfterClass
  public static void stopClient() throws IOException { httpClient.close(); }

  @Test
  public void testSimpleRead() throws ExecutionException, InterruptedException {
    assertEquals(
        PersonMap.create()
            .put$(PersonId.create(1), Person.create().setRecord(PersonRecord.create().setFirstName("First1")))
            .put$(PersonId.create(2), Person.create().setRecord(PersonRecord.create().setFirstName("First2"))),

        client.read("[1,2](:record(firstName))")
    );
  }

  @Test
  public void testReadWithMeta() throws ExecutionException, InterruptedException {
    assertEquals(
        PersonMap.create()
            .put$(PersonId.create(1), Person.create().setRecord(PersonRecord.create().setFirstName("First1")))
            .put$(PersonId.create(2), Person.create().setRecord(PersonRecord.create().setFirstName("First2")))
            .setMeta(PaginationInfo.create().setStart(1L).setCount(2L)),

        client.read(";start=1;count=2[1,2](:record(firstName))@(start,count)")
    );
  }

  @Test
  public void testPathRead() throws ExecutionException, InterruptedException {
    assertEquals(
        Person.create().setRecord(PersonRecord.create().setFirstName("First2")),
        client.readBestFriend("/1:record/bestFriend:record(firstName)")
    );
  }

  @Test
  public void testSimpleCreateWithoutProjection() throws ExecutionException, InterruptedException {
    testSimpleCreate(null);
  }

  @Test
  public void testSimpleCreateWithProjection() throws ExecutionException, InterruptedException {
    testSimpleCreate("*(firstName)");
  }

  private void testSimpleCreate(@Nullable String inputProjection) throws ExecutionException, InterruptedException {
    PersonId_List idList = client.create(
        inputProjection,
        PersonRecord_List.create().add(PersonRecord.create().setFirstName("testCreate")),
        "*"
    ).get();

    assertNotNull(idList);
    TestCase.assertEquals(1, idList.datums().size());
    PersonId id = idList.datums().iterator().next();

    assertEquals(
        PersonMap.create().put$(id, Person.create().setRecord(PersonRecord.create().setFirstName("testCreate"))),
        client.read("[" + id.getVal() + "](:record(firstName))")
    );

    client.delete("[" + id.getVal() + "]", "").get();

    assertEquals(
        PersonMap.create(), // backend doesn't return 404s for missing users, we get nulls back instead
        client.read("[" + id.getVal() + "](:record(firstName))")
    );
  }

  @Test
  public void testCreateWithPath() throws ExecutionException, InterruptedException {
    assertEquals(
        Person_List.create()
            .add(Person.create().setId_Error(new ErrorValue(400, "Friend with id 2 already exists")))
            .add(Person.create().setId(PersonId.create(3)))
            .add(Person.create().setId_Error(new ErrorValue(404, "User with id 22 not found"))),

        client.createFriends(
            "/1:record/friends",
            null,
            Person_List.create()
                .add(Person.create().setId(PersonId.create(2)))
                .add(Person.create().setId(PersonId.create(3)))
                .add(Person.create().setId(PersonId.create(22))),
            "*:id"
        )

    );
  }

  @Test
  public void testUpdateWithoutProjection() throws ExecutionException, InterruptedException {
    testSimpleUpdate(null);
  }

  @Test
  public void testUpdateWithProjection() throws ExecutionException, InterruptedException {
    testSimpleUpdate("[1,22]:record(firstName)");
  }

  private void testSimpleUpdate(String updateProjection) throws ExecutionException, InterruptedException {
    assertEquals(
        PersonId_Error_Map.create()
            .put(PersonId.create(22), Error.create().setCode(404).setMessage("User with id 22 not found")),

        client.update(
            updateProjection,
            PersonMap.create()
                .put$(PersonId.create(1), Person.create().setRecord(PersonRecord.create().setFirstName("Alfred2")))
                .put$(PersonId.create(22), Person.create().setRecord(PersonRecord.create().setFirstName("Alfred2"))),
            "[*](code,message)"
        )
    );

    assertEquals(
        PersonMap.create()
            .put$(PersonId.create(1), Person.create().setRecord(PersonRecord.create().setFirstName("Alfred2"))),

        client.read("[1](:record(firstName))")
    );

    // change back
    assertEquals(
        PersonId_Error_Map.create(),

        client.update(
            updateProjection,
            PersonMap.create()
                .put$(PersonId.create(1), Person.create().setRecord(PersonRecord.create().setFirstName("Alfred"))),
            "[*](code,message)"
        )
    );
  }

  @Test
  public void testUpdateWithPath() throws ExecutionException, InterruptedException {

    client.updateBestFriend(
        "/1:record/bestFriend",
        null,
        Person.create().setId(PersonId.create(3)),
        "(code,message)"
    ).get();

    assertEquals(
        PersonMap.create()
            .put$(
                PersonId.create(1),
                Person.create().setRecord(
                    PersonRecord.create().setBestFriend(Person.create().setId(PersonId.create(3))))
            ),

        client.read("/1:record/bestFriend:id")
    );

    // change it back
    client.updateBestFriend(
        "/1:record/bestFriend",
        null,
        Person.create().setId(PersonId.create(2)),
        "(code,message)"
    ).get();

    assertEquals(
        epigraph.Error.create().setCode(404).setMessage("User with id 21 not found"),

        client.updateBestFriend(
            "/21:record/bestFriend",
            null,
            Person.create().setId(PersonId.create(3)),
            "(code,message)"
        )
    );
  }

  @Test
  public void testDeleteWithPath() throws ExecutionException, InterruptedException {
    assertEquals(
        PersonMap.create()
            .put$(
                PersonId.create(8),
                Person.create().setRecord(
                    PersonRecord.create().setBestFriend(Person.create().setId(PersonId.create(9))))
            ),

        client.read("/8:record/bestFriend:id")
    );

    assertEquals(
        null,
        client.deleteBestFriend("/8:record/bestFriend", "", "(code,message)")
    );

    assertEquals(
        PersonMap.create()
            .put$(
                PersonId.create(8),
                Person.create().setRecord(PersonRecord.create())
            ),

        client.read("/8:record/bestFriend:id")
    );

    // change back

    assertEquals(
        null,
        client.updateBestFriend(
            "/8:record/bestFriend",
            null,
            Person.create().setId(PersonId.create(9)),
            "(code,message)"
        )
    );

    assertEquals(
        epigraph.Error.create().setCode(404).setMessage("User with id 28 not found"),
        client.updateBestFriend(
            "/28:record/bestFriend",
            null,
            Person.create().setId(PersonId.create(9)),
            "(code,message)"
        )
    );
  }

  @Test
  public void testCustomWithPath() throws ExecutionException, InterruptedException {
    PersonId_List idList = client.create(
        null,
        PersonRecord_List.create().add(PersonRecord.create().setFirstName("first").setLastName("last")),
        "*"
    ).get();

    assertNotNull(idList);
    TestCase.assertEquals(1, idList.datums().size());

    Integer id = idList.datums().iterator().next().getVal();

    assertEquals(
        PersonRecord.create().setFirstName("FIRST").setLastName("last"),
        client.customCapitalize(
            "/" + id,
            "(lastName)",
            null,
            "(firstName,lastName)"
        )
    );

    client.delete("[" + id + "]", "");
  }

  @Test
  public void testComplexParams() throws ExecutionException, InterruptedException {
    @NotNull CompletableFuture<PersonMap> future = client.customEcho(
        ";param=ws.epigraph.tests.PersonMap(1:<id:1,record:{id:1,firstName:'{foo}\\'+\"[bar]',friends:[<id:2>]}>)",
        null,
        "[*]:(id,record(id,firstName,bestFriend:id,friends*:id))"
    );

    assertEquals(
        PersonMap.create()
            .put$(
                PersonId.create(1),
                Person.create()
                    .setId(PersonId.create(1))
                    .setRecord(
                        PersonRecord.create()
                            .setId(PersonId.create(1))
                            .setFirstName("{foo}'+\"[bar]")
                            .setFriends(
                                Person_List.create()
                                    .add(Person.create().setId(PersonId.create(2)))
                            )
                    )
            ),
        future
    );
  }

  private void assertEquals(Data expected, Future<? extends Data> actualFuture)
      throws ExecutionException, InterruptedException {

    Data actual = actualFuture.get();

    if (!expected.equals(actual)) {
      String expectedStr = printData(expected);
      String actualStr = printData(actual);

      if (expected.equals(actual))
        fail("Broken equals() implementation!");
      else
        TestCase.assertEquals(expectedStr, actualStr); // will show nice diff in idea
    }
  }

  private void assertEquals(Datum expected, Future<? extends Datum> actualFuture)
      throws ExecutionException, InterruptedException {

    Datum actual = actualFuture.get();
    String actualStr = printDatum(actual);

    if (expected == null) {
      TestCase.assertNull(actualStr, actual);
    } else if (!expected.equals(actual)) {
      String expectedStr = printDatum(expected);

      if (expected.equals(actual))
        fail("Broken equals() implementation!");
      else
        TestCase.assertEquals(expectedStr, actualStr); // will show nice diff in idea
    }
  }

  private String printData(final Data data) {
    String dataToString;
    try {
      StringWriter sw = new StringWriter();
      DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
      printer.print(data);
      dataToString = sw.toString();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.toString());
      dataToString = null;
    }
    return dataToString;
  }

  @Contract("null -> !null")
  private String printDatum(final Datum datum) {
    if (datum == null) return "null";
    String dataToString;
    try {
      StringWriter sw = new StringWriter();
      DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
      printer.print(datum.type(), datum);
      dataToString = sw.toString();
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.toString());
      dataToString = null;
    }
    return dataToString;
  }

  protected static @NotNull Service buildUsersService() throws ServiceInitializationException {
    return new Service(
        "users",
        Arrays.asList(
            new UserResourceFactory().getUserResource(),
            new UsersResourceFactory(new UsersStorage()).getUsersResource()
        )
    );
  }
}
