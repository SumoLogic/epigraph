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

package ws.epigraph.server.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.server.http.undertow.UndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.Person;
import ws.epigraph.tests.UserResourceFactory;
import ws.epigraph.tests.UsersResourceFactory;
import ws.epigraph.tests.UsersStorage;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHttpServerTest {
  private static final int PORT = 8888;
  private static final String HOST = "localhost";
  private static final int TIMEOUT = 100; // ms

  private static final String URL_PREFIX = "http://" + HOST + ":" + PORT + "/";

  private static final TypesResolver resolver = IndexBasedTypesResolver.INSTANCE;

  private static Undertow server;

  private static @NotNull Service buildUsersService() throws ServiceInitializationException {
    return new Service(
        "users",
        Arrays.asList(
            new UserResourceFactory().getUserResource(),
            new UsersResourceFactory(new UsersStorage()).getUsersResource()
        )
    );
  }

  @Test
  public void testEmptyRequest() throws UnirestException {
    get("", 400, "Bad URL format. Supported resources: {/user, /users}");
  }

  @Test
  public void testSimpleGet() throws UnirestException {
    get("/users/1:record(id,firstName)", 200, "{'id':1,'firstName':'First1'}");
  }

  @Test
  public void testPolymorphicGet() throws UnirestException {
    get(
        "users[4,5](:record(id,firstName,lastName,bestFriend:record(id)~~User:record(profile)))",
        200,
        "[{'K':4,'V':" +
        "{'id':4,'firstName':'First4','lastName':'Last4','bestFriend':" +
        "{'type':'ws.epigraph.tests.User','data':{'id':5,'profile':'http://google.com/5'}}}}," +
        "{'K':5,'V':" +
        "{'id':5,'firstName':'First5','lastName':'Last5','bestFriend':" +
        "{'type':'ws.epigraph.tests.Person','data':{'id':6}}}}]"
    );
  }

  @Test
  public void testGetWithMeta() throws UnirestException {
    get(
        "users;start=5;count=10[1](:id)@(start,count)", // todo params should belong to model, not field
        200,
        "{\"meta\":{\"start\":5,\"count\":10},\"data\":[{\"K\":1,\"V\":1}]}"
    );
  }

  @Test
  public void testCreateReadUpdateDelete() throws UnirestException {
    Integer id = Integer.parseInt(post(null, "users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 1;

    get("users/" + id + ":record(firstName)", 200, "{'firstName':'Alfred'}");
    put("users<[" + id + "]:record(firstName)", "[{'K':11,'V':{'firstName':'Bruce'}}]", 200, "[]");
    get("users/" + id + ":record(firstName)", 200, "{'firstName':'Bruce'}");
    delete(
        "users<[" + id + "," + nextId + "]>[*](code,message)",
        200,
        "[{\"K\":" + nextId + ",\"V\":{\"code\":404,\"message\":\"Item with id " + nextId + " doesn't exist\"}}]"
    );
  }

  @Test
  public void testCustom() throws UnirestException {
    Integer id = Integer.parseInt(post(null, "users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 1;

    post(
        "capitalize",
        "users/" + nextId,
        "{'firstName':'Alfred'}",
        200,
        "\\{'ERROR':404,'message':'Person with id " + nextId + " not found'\\}"
    );

    post("capitalize", "users/" + id + "<(firstName)>(firstName,lastName)", null, 200, "\\{'firstName':'ALFRED'\\}");
    delete(
        "users<[" + id + "]>[*](code,message)",
        200,
        "[]"
    );
    Person p = Person.create();
    p.toImmutable();
  }

  public static void main(String[] args) throws ServiceInitializationException {
    start();
  }

  @BeforeClass
  public static void start() throws ServiceInitializationException {
    server = Undertow.builder()
        .addHttpListener(PORT, HOST)
        .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
//        .setHandler(new UndertowHandler_Old(buildUsersService(), resolver, TIMEOUT))
        .setHandler(new UndertowHandler(buildUsersService(), resolver, TIMEOUT))
        .build();

    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop();
  }

  private void get(String request, int expectedStatus, String expectedBody) throws UnirestException {
    final HttpResponse<String> response = Unirest.get(URL_PREFIX + request).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody.replace("'", "\""), actualBody);
  }

  private Matcher post(
      String operationName,
      String requestUrl,
      String requestBody,
      int expectedStatus,
      String expectedBodyRegex)
      throws UnirestException {

    HttpRequestWithBody requestWithBody = Unirest.post(URL_PREFIX + requestUrl);
    if (operationName != null)
      requestWithBody = requestWithBody.header(RequestHeaders.OPERATION_NAME, operationName);

    final BaseRequest request =
        requestBody == null ? requestWithBody : requestWithBody.body(requestBody.replaceAll("'", "\""));

    final HttpResponse<String> response = request.asString();

    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    Pattern p = Pattern.compile(expectedBodyRegex.replace("'", "\""));
    final Matcher matcher = p.matcher(actualBody);
    assertTrue(matcher.matches());
    return matcher;
  }

  private void put(String requestUrl, String requestBody, int expectedStatus, String expectedBody)
      throws UnirestException {

    final HttpResponse<String> response =
        Unirest.put(URL_PREFIX + requestUrl).body(requestBody.replaceAll("'", "\"")).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody.replace("'", "\""), actualBody);
  }

  private void delete(String requestUrl, int expectedStatus, String expectedBody)
      throws UnirestException {

    final HttpResponse<String> response = Unirest.delete(URL_PREFIX + requestUrl).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody/*.replace("'", "\"")*/, actualBody);
  }

}
