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

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.apache.http.HttpHost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.client.http.*;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.DefaultOperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.server.http.undertow.EpigraphUndertowHandler;
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
public class AbstractHttpClientTest {
  protected static final int PORT = 8888;
  protected static final String HOST = "localhost";
  protected static final int TIMEOUT = 100; // ms
  protected static final Charset CHARSET = StandardCharsets.UTF_8;

  protected static final TypesResolver resolver = IndexBasedTypesResolver.INSTANCE;
  protected static final ResourceDeclaration resourceDeclaration = UsersResourceDeclaration.INSTANCE;
  protected static HttpRequestDispatcher dispatcher;

  protected final HttpHost httpHost = new HttpHost(HOST, PORT);
  protected final ServerProtocol serverProtocol = new FormatBasedServerProtocol(JsonFormatFactories.INSTANCE, CHARSET);

  // <todo test all 3 servers via subclassing>
  private static Undertow server;

  @BeforeClass
  public static void start() throws ServiceInitializationException {
    server = Undertow.builder()
        .addHttpListener(PORT, HOST)
        .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
        .setHandler(new EpigraphUndertowHandler(buildUsersService(), TIMEOUT))
        .build();

    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop();
  }
  // </todo>

  @Test
  public void testSimpleRead() throws ExecutionException, InterruptedException {
    testSuccessfulRead(
        UsersResourceDeclaration.readOperationDeclaration,
        "[1,2](:record(firstName))",
        "( 1: < record: { firstName: \"First1\" } >, 2: < record: { firstName: \"First2\" } > )"
    );
  }

  protected void testSuccessfulRead(
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      @NotNull String expectedDataPrint) throws ExecutionException, InterruptedException {

    OperationInvocationResult<ReadOperationResponse<?>> invocationResult =
        runReadOperation(operationDeclaration, requestString);

    invocationResult.consume(
        ror -> {
          Data data = ror.getData();
          try {
            StringWriter sw = new StringWriter();
            DataPrinter<IOException> printer = DataPrinter.toString(120, false, sw);
            printer.print(data);
            assertEquals(expectedDataPrint, sw.toString());
          } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
          }
        },

        oir -> fail(String.format("[%d] %s", oir.statusCode(), oir.message()))
    );

  }

  protected @NotNull OperationInvocationResult<ReadOperationResponse<?>> runReadOperation(
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString) throws ExecutionException, InterruptedException {

    RemoteReadOperationInvocation inv = new RemoteReadOperationInvocation(
        httpHost,
        dispatcher,
        resourceDeclaration.fieldName(),
        operationDeclaration,
        serverProtocol,
        CHARSET
    );

    OperationInvocationContext opctx = new DefaultOperationInvocationContext(true, new EBean());
    ReadOperationRequest request = constructReadRequest(
        operationDeclaration,
        requestString,
        resolver
    );

    return inv.invoke(opctx, request).get();
  }

  @BeforeClass
  public static void start2() throws IOReactorException {
    // todo test both sync/async via subclassing
    dispatcher = new AsyncHttpRequestDispatcher(
        ConnectionConfig.DEFAULT,
        IOReactorConfig.DEFAULT,
        2,
        TIMEOUT
    );
  }

  @AfterClass
  public static void stop2() throws IOException {
    dispatcher.shutdown();
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
