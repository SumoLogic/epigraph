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

import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.client.http.FormatBasedServerProtocol;
import ws.epigraph.client.http.HttpRequestDispatcher;
import ws.epigraph.client.http.RemoteReadOperationInvocation;
import ws.epigraph.client.http.ServerProtocol;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.DefaultOperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.UserResourceFactory;
import ws.epigraph.tests.UsersResourceFactory;
import ws.epigraph.tests.UsersStorage;
import ws.epigraph.tests.resources.users.UsersResourceDeclaration;
import ws.epigraph.util.EBean;
import ws.epigraph.wire.json.JsonFormatFactories;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static ws.epigraph.client.http.RequestFactory.constructReadRequest;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractHttpClientTest {
  protected static final int PORT = 8888;
  protected static final String HOST = "localhost";
  protected static final int TIMEOUT = 100; // ms
  protected static final Charset CHARSET = StandardCharsets.UTF_8;

  protected static final TypesResolver resolver = IndexBasedTypesResolver.INSTANCE;
  protected static final ResourceDeclaration resourceDeclaration = UsersResourceDeclaration.INSTANCE;
  protected static HttpRequestDispatcher dispatcher;

  protected final HttpHost httpHost = new HttpHost(HOST, PORT);
  protected final ServerProtocol serverProtocol = new FormatBasedServerProtocol(JsonFormatFactories.INSTANCE, CHARSET);

  @Test
  public void testSimpleRead() throws ExecutionException, InterruptedException {
    testSuccessfulRead(
        UsersResourceDeclaration.readOperationDeclaration,
        "[1,2](:record(firstName))",
        "( 1: < record: { firstName: \"First1\" } >, 2: < record: { firstName: \"First2\" } > )"
    );
  }

  @Test
  public void testMalformedUrl() throws ExecutionException, InterruptedException {
    testReadError(
        UsersResourceDeclaration.readOperationDeclaration,
        "[1,2](:record(firstName))",
        400,
        "Resource 'xxx' not found. Supported resources: {/user, /users}",
        "/xxx"
    );
  }

  @Test
  public void testPathError() throws ExecutionException, InterruptedException {
    testReadError(
        UsersResourceDeclaration.bestFriendReadOperationDeclaration,
        "/12:record/bestFriend:record(firstName)",
        404,
        "{\"ERROR\":404,\"message\":\"User with id 12 not found\"}",
        null
    );
  }

  @Test
  public void testPathRead() throws ExecutionException, InterruptedException {
    testSuccessfulRead(
        UsersResourceDeclaration.bestFriendReadOperationDeclaration,
        "/1:record/bestFriend:record(firstName)",
        "< record: { firstName: \"First2\" } >"
    );
  }

  protected void testSuccessfulRead(
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      @NotNull String expectedDataPrint) throws ExecutionException, InterruptedException {

    OperationInvocationResult<ReadOperationResponse<?>> invocationResult =
        runReadOperation(operationDeclaration, requestString, null);

    invocationResult.consume(
        ror -> {
          Data data = ror.getData();
          String dataToString = printData(data);
          assertEquals(expectedDataPrint, dataToString);
        },

        oir -> fail(String.format("[%d] %s", oir.statusCode(), oir.message()))
    );
  }

  protected void testReadError(
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      int expectedStatusCode,
      @NotNull String expectedError,
      @Nullable String uriOverride) throws ExecutionException, InterruptedException {

    OperationInvocationResult<ReadOperationResponse<?>> invocationResult =
        runReadOperation(operationDeclaration, requestString, uriOverride);

    invocationResult.consume(
        ror -> fail("Expected request to fail, got: " + printData(ror.getData())),

        oir -> {
          assertEquals(oir.message(), expectedStatusCode, oir.statusCode());
          assertEquals(expectedError, oir.message());
        }
    );

  }

  protected @NotNull OperationInvocationResult<ReadOperationResponse<?>> runReadOperation(
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      @Nullable String requestUri) throws ExecutionException, InterruptedException {

    RemoteReadOperationInvocation inv = new RemoteReadOperationInvocation(
        httpHost,
        dispatcher,
        resourceDeclaration.fieldName(),
        operationDeclaration,
        serverProtocol,
        CHARSET) {
      @Override
      protected @NotNull String composeUri(final @NotNull ReadOperationRequest request) {
        return requestUri == null ? super.composeUri(request) : requestUri;
      }
    };

    OperationInvocationContext opctx = new DefaultOperationInvocationContext(true, new EBean());
    ReadOperationRequest request = constructReadRequest(
        resourceDeclaration.fieldType(),
        operationDeclaration,
        requestString,
        resolver
    );

    return inv.invoke(opctx, request).get();
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
