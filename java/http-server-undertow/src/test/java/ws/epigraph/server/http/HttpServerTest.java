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

package ws.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import epigraph.PersonId_Error_Map;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.idl.Edl;
import ws.epigraph.idl.parser.EdlPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.server.http.undertow.UndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class HttpServerTest {
  private static final int PORT = 8888;
  private static final String HOST = "localhost";
  private static final int TIMEOUT = 100; // ms

  private static final String URL_PREFIX = "http://" + HOST + ":" + PORT + "/";

  private static final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      PersonRecord.type,
      User.type,
      UserId.type,
      UserRecord.type,
      PersonId_Person_Map.type,
      PersonRecord_List.type,
      PersonId_Error_Map.type,
      PersonId_List.type,
      epigraph.Error.type,
      epigraph.String.type
  );

  private static final Edl edl;

  private static Undertow server;

  static {
    try {
      edl = parseIdlResource("/ws/epigraph/tests/service/testService.esc");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static @NotNull Service buildUsersService() throws ServiceInitializationException {
    return new Service(
        "users",
        Arrays.asList(
            new UserResource(edl.resources().get("user")),
            new UsersResource(edl.resources().get("users"), new UsersStorage())
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
        "users[4,5](:record(id,firstName,lastName,bestFriend:record(id)~User:record(profile)))",
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

//  private static @NotNull Idl parseIdlText(@NotNull String text) throws IOException {
//    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
//    IdlFile psiFile = (IdlFile) EpigraphPsiUtil.parseFile("dummy.idl", text, IdlParserDefinition.INSTANCE, errAcc);
//    return parseIdl(psiFile, errAcc);
//  }

  private static @NotNull Edl parseIdlResource(@NotNull String resourcePath) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
    SchemaFile psiFile =
        (SchemaFile) EpigraphPsiUtil.parseResource(resourcePath, SchemaParserDefinition.INSTANCE, errAcc);
    return parseEdl(psiFile, errAcc);
  }

  private static @NotNull Edl parseEdl(@NotNull SchemaFile psiFile, EpigraphPsiUtil.ErrorsAccumulator errAcc) {

    if (errAcc.hasErrors()) {
      for (PsiErrorElement element : errAcc.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      throw new RuntimeException(DebugUtil.psiTreeToString(psiFile, true));
    }

    Edl edl = null;
    List<PsiProcessingError> errors = new ArrayList<>();
    try {
      edl = EdlPsiParser.parseEdl(psiFile, resolver, errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.println(error.message() + " at " + error.location());
      }

      throw new RuntimeException("IDL errors detected");
    }

    assert edl != null;
    return edl;
  }

}
