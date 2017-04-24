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
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.Person;
import ws.epigraph.tests.UserResourceFactory;
import ws.epigraph.tests.UsersResourceFactory;
import ws.epigraph.tests.UsersStorage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractHttpServerTest {
  protected static final int PORT = 8888;
  protected static final String HOST = "localhost";
  protected static final int TIMEOUT = 100; // ms

  protected static @NotNull Service buildUsersService() throws ServiceInitializationException {
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
  public void testSimplePrimitiveGet() throws UnirestException {
    get("/users/1:id", 200, "1");
  }

  @Test
  public void testSimpleNotFound() throws UnirestException {
    get("/users/123:id", 404, "{'ERROR':404,'message':'key ''123'' not found'}");
  }

  @Test
  public void testSimpleGet() throws UnirestException {
    get("/users/1:record(id,firstName)", 200, "{'id':1,'firstName':'First1'}");
  }

  @Test
  public void testPolymorphicGet() throws UnirestException {
    get(
        "/users[4,5](:record(id,firstName,lastName,bestFriend:record(id)~~User:record(profile)))",
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
        "/users;start=5;count=10[1](:id)@(start,count)",
        200,
        "{\"meta\":{\"start\":5,\"count\":10},\"data\":[{\"K\":1,\"V\":1}]}"
    );
  }

  @Test
  public void testPathPolyGet() throws UnirestException {
    get(
        "/user:record(firstName)~~User:record(profile)",
        200,
        "{'firstName':'Alfred','profile':{'ERROR':404,'message':'Not Found'}}"
    );
  }

  @Test
  public void testPathPolyGetRequired() throws UnirestException {
    get(
        "/user:record(firstName)~~User:record(+profile)",
        412,
        ":record/profile : Required value is a [404] error: Not Found"
    );
  }

  @Test
  public void testPolyRequiredButAbsent() throws UnirestException {
    get(
        "/user:record(friends*(:record(firstName)~~User:record(+profile)))",
        520,
        ":record/friends[2]:record : Required field ''profile'' is missing"
    );
  }

  @Test
  public void testRequiredInsideOptional() throws UnirestException {
    get(
        "/users/10:record(+firstName,worstEnemy(+firstName))",
        200,
        "{'firstName':'First10'}"
    );
  }

  @Test
  public void testCreateReadUpdateDelete() throws UnirestException {
    Integer id = Integer.parseInt(post(null, "/users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 1;

    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Alfred'}");
    put("/users<[" + id + "]:record(firstName)", "[{'K':11,'V':{'firstName':'Bruce'}}]", 200, "[]");
    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Bruce'}");
    delete(
        "/users<[" + id + "," + nextId + "]>[*](code,message)",
        200,
        "[{\"K\":" + nextId + ",\"V\":{\"code\":404,\"message\":\"Item with id " + nextId + " doesn't exist\"}}]"
    );
  }

  @Test
  public void testCustom() throws UnirestException {
    Integer id = Integer.parseInt(post(null, "/users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 1;

    post(
        "capitalize",
        "/users/" + nextId,
        "{'firstName':'Alfred'}",
        200,
        "\\{'ERROR':404,'message':'Person with id " + nextId + " not found'\\}"
    );

    post("capitalize", "/users/" + id + "<(firstName)>(firstName,lastName)", null, 200, "\\{'firstName':'ALFRED'\\}");
    delete(
        "/users<[" + id + "]>[*](code,message)",
        200,
        "[]"
    );
    Person p = Person.create();
    p.toImmutable();
  }

  private void get(String requestUri, int expectedStatus, String expectedBody) throws UnirestException {
    final HttpResponse<String> response = Unirest.get(url(requestUri)).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());

    String eb = expectedBody.replace("''", "@@@");
    eb = eb.replace("'", "\"");
    eb = eb.replace("@@@", "'");

    assertEquals(eb, actualBody);
  }

  private Matcher post(
      String operationName,
      String requestUri,
      String requestBody,
      int expectedStatus,
      String expectedBodyRegex)
      throws UnirestException {

    HttpRequestWithBody requestWithBody = Unirest.post(url(requestUri));
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

  private void put(String requestUri, String requestBody, int expectedStatus, String expectedBody)
      throws UnirestException {

    final HttpResponse<String> response =
        Unirest.put(url(requestUri)).body(requestBody.replaceAll("'", "\"")).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody.replace("'", "\""), actualBody);
  }

  private void delete(String requestUri, int expectedStatus, String expectedBody)
      throws UnirestException {

    final HttpResponse<String> response = Unirest.delete(url(requestUri)).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody/*.replace("'", "\"")*/, actualBody);
  }

  private String url(String requestUri) { return url(requestUri, null); }

  private String url(String requestUri, String query) {
    try {
      URI uri = new URI("http", null, HOST, PORT, requestUri, query, null);
      String uriString = uri.toURL().toString();

      // unirest bug https://github.com/Mashape/unirest-java/issues/158 (converts plus to %20 in path)
      uriString = uriString.replace("+", "%2b");

      return uriString;
    } catch (URISyntaxException | MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
