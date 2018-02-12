/*
 * Copyright 2018 Sumo Logic
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.http.ContentType;
import ws.epigraph.http.ContentTypes;
import ws.epigraph.http.EpigraphHeaders;
import ws.epigraph.http.Headers;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.Person;
import ws.epigraph.tests.UserResourceFactory;
import ws.epigraph.tests.UsersResourceFactory;
import ws.epigraph.tests.UsersStorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractHttpServerTest {
  protected static final AtomicInteger UNIQUE_PORT = new AtomicInteger(8888);
  protected static final String HOST = "localhost";
  protected static final int TIMEOUT = 100; // ms

  protected abstract int port();

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
  public void testEmptyRequest() throws IOException {
    get("", 400, "Bad URL format. Supported resources: {/user, /users}", ContentTypes.TEXT_UTF8);
  }

  @Test
  public void testSimplePrimitiveGet() throws IOException {
    get("/users/1:id", 200, "1");
  }

  @Test
  public void testSimpleNotFound() throws IOException {
    get("/users/123:id", 404, "{'ERROR':404,'message':'User ''123'' not found'}");
  }

  @Test
  public void testSimpleNotFound2() throws IOException {
    get("/users/123:record/firstName", 404, "{'ERROR':404,'message':'User ''123'' not found'}");
  }

  @Test
  public void testSimpleGet() throws IOException {
    get("/users/1:record(id,firstName)", 200, "{'id':1,'firstName':'First1'}");
  }

  @Test
  public void testGetWithDefault() throws IOException {
    get(
        "/users/1",
        200,
        "{\"firstName\":\"First1\",\"lastName\":\"Last1\",\"friendsMap\":[{\"K\":\"Alfred\",\"V\":{\"id\":1,\"record\":{\"firstName\":{\"ERROR\":404,\"message\":\"not found\"}}}}],\"friendRecordMap\":[{\"K\":\"Alfred\",\"V\":{\"firstName\":{\"ERROR\":404,\"message\":\"not found\"}}}]}"
    );
  }

  @Test
  public void testPolymorphicGet() throws IOException {
    get(
        "/users[4,5](:record(id,firstName,lastName,bestFriend:record(id):~User:record(profile)))",
        200,
        "[{'K':4,'V':" +
        "{'id':4,'firstName':'First4','lastName':'Last4','bestFriend':" +
        "{'TYPE':'ws.epigraph.tests.User','DATA':{'id':5,'profile':'http://google.com/5'}}}}," +
        "{'K':5,'V':" +
        "{'id':5,'firstName':'First5','lastName':'Last5','bestFriend':" +
        "{'TYPE':'ws.epigraph.tests.Person','DATA':{'id':6}}}}]"
    );
  }

  @Test
  public void testGetWithMeta() throws IOException {
    get(
        "/users@(start,count);start=5;count=10[1]:id",
        200,
        "{\"META\":{\"start\":5,\"count\":10},\"DATA\":[{\"K\":1,\"V\":1}]}"
    );
  }

  @Test
  public void testPathPolyGet() throws IOException {
    get(
        "/user:record(firstName):~User:record(profile)",
        200,
        "{'firstName':'Alfred','profile':{'ERROR':404,'message':'Not Found'}}"
    );
  }

  @Test
  public void testPathPolyGetRequired() throws IOException {
    get(
        "/user:record(firstName):~User:record(+profile)",
        412,
        // this is /user:record value
        "{\"ERROR\":412,\"message\":\":record/profile : Required data is a [404] error: Not Found\"}"
    );
  }

  @Test
  public void testPolyRequiredButAbsent() throws IOException {
    get(
        "/user:record(friends*(:record(firstName):~User:record(+profile)))",
        520,
        ":record/friends[2]:record : Required field ''profile'' is missing",
        ContentTypes.TEXT_UTF8
    );
  }

  @Test
  public void testRequiredInsideMapInPath() throws IOException {
    get(
        "/users/10:record/friendRecordMap/'Alfred'(+firstName)",
        412,
        "{'ERROR':412,'message':'[10]:record/friendRecordMap[''Alfred'']/firstName : Required data is a [404] error: not found'}"
    );
  }

  @Test
  public void testRequiredInsideOptional() throws IOException {
    get(
        "/users/10:record(+firstName,worstEnemy(+firstName))",
        200,
        "{'firstName':'First10'}"
    );
  }

  @Test
  public void testGetWithPath() throws IOException {
    get(
        "/users/1:record/bestFriend:record(firstName)",
        200,
        "{'firstName':'First2'}"
    );

    get(
        "/users/12:record/bestFriend:record(firstName)",
        404,
        "{'ERROR':404,'message':'User with id 12 not found'}"
    );
  }

  @Test
  public void testCreateReadUpdateDelete() throws IOException {
    Integer id = Integer.parseInt(post(null, "/users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 100;

    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Alfred'}");
    put("/users[" + id + "]:record(firstName)", "[{'K':" + id + ",'V':{'firstName':'Bruce'}}]", 200, "[]", false);
    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Bruce'}");
    delete(
        "/users[" + id + "," + nextId + "]>[*](code,message)",
        200,
        "[{\"K\":" + nextId + ",\"V\":{\"code\":404,\"message\":\"Item with id " + nextId + " doesn't exist\"}}]"
    );
  }

  @Test
  public void testCreateReadUpdateDeleteWithPath() throws IOException {
    Integer id = Integer.parseInt(post(null, "/users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 100;

    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Alfred'}");
    put("/users/" + id + ":record/firstName", "'Bruce'", 200, "[]", false);
    get("/users/" + id + ":record(firstName)", 200, "{'firstName':'Bruce'}");
    delete("/users/" + id + ">[*](code,message)", 200, "[]");
    delete(
        "/users/" + nextId + ">/" + nextId + "(code,message)",
        200,
        "{\"code\":404,\"message\":\"Item with id " + nextId + " doesn't exist\"}"
    );
  }

  @Test
  public void testCreateWithRequired() throws IOException {
    post(
        null,
        "/users",
        "[{'lastName':'Foo'}]",
        400,
        "Error reading create request body: Required field ''firstName'' is missing at line 1 column 20"
    );
  }

  @Test
  public void testUpdateWithRequiredNoReqProjection() throws IOException {
    put(
        "/users",
        "[{'K':1,'V':{'record':{'lastName':'Bruce2'}}}]",
        400,
        "Error reading update request body: Required field ''firstName'' is missing at line 1 column 44",
        false
    );
  }

  @Test
  public void testUpdateWithRequiredReqProjection() throws IOException {
    put(
        "/users<[1]:record(lastName)",
        "[{'K':1,'V':{'record':{'lastName':'Bruce2'}}}]",
        400,
        ".*",
        true
    );
  }

  @Test
  public void testCustom() throws IOException {
    Integer id = Integer.parseInt(post(null, "/users", "[{'firstName':'Alfred'}]", 201, "\\[(\\d+)\\]").group(1));
    int nextId = id + 100;

    post(
        "capitalize",
        "/users/" + nextId,
        "{'firstName':'Alfred'}",
        200,
        "\\{'ERROR':404,'message':'Person with id " + nextId + " not found'\\}"
    );

    post("capitalize", "/users/" + id + "(firstName)>(firstName,lastName)", null, 200, "\\{'firstName':'ALFRED'\\}");
    delete(
        "/users[" + id + "]>[*](code,message)",
        200,
        "[]"
    );
    Person p = Person.create();
    p.toImmutable();
  }

  protected void get(String requestUri, int expectedStatus, String expectedBody) throws IOException {
    get(requestUri, expectedStatus, expectedBody, ContentTypes.JSON_UTF8);
  }

  protected void get(String host, int port, String requestUri, int expectedStatus, String expectedBody)
      throws IOException {
    get(host, port, requestUri, expectedStatus, expectedBody, ContentTypes.JSON_UTF8);
  }

  protected void get(String requestUri, int expectedStatus, String expectedBody, ContentType expectedContentType)
      throws IOException {

    get(HOST, port(), requestUri, expectedStatus, expectedBody, expectedContentType);
  }

  protected void get(
      String host,
      int port,
      String requestUri,
      int expectedStatus,
      String expectedBody,
      ContentType expectedContentType)
      throws IOException {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(url(host, port, requestUri, null));
    final CloseableHttpResponse response = httpClient.execute(httpGet);

    int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    String actualBody = EntityUtils.toString(entity).trim();
    response.close();
    httpClient.close();

    assertEquals(actualBody, expectedStatus, status);

    Header contentType = response.getFirstHeader(Headers.CONTENT_TYPE);
    assertNotNull(contentType);
    assertEquals(expectedContentType.toString(), contentType.getValue());

    String eb = unescape(expectedBody);
    assertEquals(eb, actualBody);
  }

  private Matcher post(
      String operationName,
      String requestUri,
      String requestBody,
      int expectedStatus,
      String expectedBodyRegex) throws IOException {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url(requestUri));
    if (operationName != null)
      httpPost.addHeader(EpigraphHeaders.OPERATION_NAME, operationName);
    if (requestBody != null)
      httpPost.setEntity(new StringEntity(unescape(requestBody)));
    CloseableHttpResponse response = httpClient.execute(httpPost);

    int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    String actualBody = EntityUtils.toString(entity).trim();
    response.close();
    httpClient.close();

    assertEquals(actualBody, expectedStatus, status);
    Pattern p = Pattern.compile(unescape(expectedBodyRegex));
    final Matcher matcher = p.matcher(actualBody);
    assertTrue("actual body: <" + actualBody + ">", matcher.matches());
    return matcher;
  }

  private void put(String requestUri, String requestBody, int expectedStatus, String expectedBody, boolean isBodyRegex)
      throws IOException {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(url(requestUri));
//    if (operationName != null)
//      httpPost.addHeader(RequestHeaders.OPERATION_NAME, operationName);
    if (requestBody != null)
      httpPut.setEntity(new StringEntity(unescape(requestBody)));
    CloseableHttpResponse response = httpClient.execute(httpPut);

    int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    String actualBody = EntityUtils.toString(entity).trim();
    response.close();
    httpClient.close();

    assertEquals(actualBody, expectedStatus, status);

    final String expected = unescape(expectedBody);
    if (isBodyRegex) {
      Pattern p = Pattern.compile(expected, Pattern.DOTALL);
      final Matcher matcher = p.matcher(actualBody);
      assertTrue(actualBody, matcher.matches());
    } else {
      assertEquals(expected, actualBody);
    }
  }

  private void delete(String requestUri, int expectedStatus, String expectedBody) throws IOException {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(url(requestUri));
//    if (operationName != null)
//      httpPost.addHeader(RequestHeaders.OPERATION_NAME, operationName);
    CloseableHttpResponse response = httpClient.execute(httpDelete);

    int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    String actualBody = EntityUtils.toString(entity).trim();
    response.close();
    httpClient.close();

    assertEquals(actualBody, expectedStatus, status);
    assertEquals(expectedBody/*.replace("'", "\"")*/, actualBody);
  }

  private String url(String requestUri) { return url(requestUri, null); }

  private String url(String requestUri, String query) { return url(HOST, port(), requestUri, query); }

  private String url(String host, int port, String requestUri, String query) {
    try {
      URI uri = new URI("http", null, host, port, requestUri, query, null);
      return uri.toURL().toString();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private String unescape(final String expectedBody) {
    String eb = expectedBody.replace("''", "@@@");
    eb = eb.replace("'", "\"");
    eb = eb.replace("@@@", "'");
    return eb;
  }

}
