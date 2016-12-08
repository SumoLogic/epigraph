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
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.parser.IdlParserDefinition;
import ws.epigraph.idl.parser.IdlPsiParser;
import ws.epigraph.idl.parser.psi.IdlFile;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.server.http.undertow.UndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

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
      epigraph.String.type
  );

  private static final Idl idl;

  private static Undertow server;

  static {
    try {
      idl = parseIdlResource("/ws/epigraph/tests/service/testService.sdl");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static @NotNull Service buildUsersService() throws ServiceInitializationException {
    return new Service(
        "users",
        Arrays.asList(
            new UserResource(idl.resources().get("user")),
            new UsersResource(idl.resources().get("users"), new UsersStorage())
        )
    );
  }

  @Test
  public void testEmptyRequest() throws UnirestException {
    testReadRequest("", 400, "Bad URL format. Supported resources: {/user, /users}");
  }

  @Test
  public void testSimpleGet() throws UnirestException {
    testReadRequest("/users/1:record(id,firstName)", 200, "{'id':1,'firstName':'First1'}");
  }

  @Test
  public void testPolymorphicGet() throws UnirestException {
    testReadRequest(
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
  public void testCreateNoProjection() throws UnirestException {
    // todo create op should return back an array of id's and correct status code
    testCreateRequest("users", "[{'firstName':'Alfred'}]", 200, "\"inserted 1 items\"");
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

  private void testReadRequest(String request, int expectedStatus, String expectedBody) throws UnirestException {
    final HttpResponse<String> response = Unirest.get(URL_PREFIX + request).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody.replace("'", "\""), actualBody);
  }

  private void testCreateRequest(String requestUrl, String requestBody, int expectedStatus, String expectedBody)
      throws UnirestException {

    final HttpResponse<String> response =
        Unirest.post(URL_PREFIX + requestUrl).body(requestBody.replaceAll("'", "\"")).asString();
    final String actualBody = response.getBody().trim();
    assertEquals(actualBody, expectedStatus, response.getStatus());
    assertEquals(expectedBody.replace("'", "\""), actualBody);
  }

//  private static @NotNull Idl parseIdlText(@NotNull String text) throws IOException {
//    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
//    IdlFile psiFile = (IdlFile) EpigraphPsiUtil.parseFile("dummy.idl", text, IdlParserDefinition.INSTANCE, errAcc);
//    return parseIdl(psiFile, errAcc);
//  }

  private static @NotNull Idl parseIdlResource(@NotNull String resourcePath) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
    IdlFile psiFile = (IdlFile) EpigraphPsiUtil.parseResource(resourcePath, IdlParserDefinition.INSTANCE, errAcc);
    return parseIdl(psiFile, errAcc);
  }

  private static @NotNull Idl parseIdl(@NotNull IdlFile psiFile, EpigraphPsiUtil.ErrorsAccumulator errAcc) {

    if (errAcc.hasErrors()) {
      for (PsiErrorElement element : errAcc.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      throw new RuntimeException(DebugUtil.psiTreeToString(psiFile, true));
    }

    Idl idl = null;
    List<PsiProcessingError> errors = new ArrayList<>();
    try {
      idl = IdlPsiParser.parseIdl(psiFile, resolver, errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }

      throw new RuntimeException("IDL errors detected");
    }

    assert idl != null;
    return idl;
  }

}
