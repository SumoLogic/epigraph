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

package ws.epigraph.server.http.servlet;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ws.epigraph.server.http.AbstractHttpServerTest;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;

import javax.servlet.ServletConfig;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ProhibitedExceptionDeclared")
public class AsyncEpigraphServletHttpServerTest extends AbstractHttpServerTest {
  private static Server jettyServer;

  private static final int port = UNIQUE_PORT.incrementAndGet();

  @Override
  protected int port() { return port; }

  public static void main(String[] args) throws Exception {
    start();
//    jettyServer = startWithRewrite(PORT);
    jettyServer.join();
  }

  @BeforeClass
  public static void start() throws Exception {
    jettyServer = new Server(port);

    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(TestServlet.class, "/*");

    jettyServer.setHandler(handler);
    jettyServer.start();
  }

  // example of setting up URI rewrite filter before Epigraph servlet
  public static Server startWithRewrite(int port) throws Exception {
    Server server = new Server(port);

    ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    contextHandler.setContextPath("/api");
    contextHandler.addServlet(TestServlet.class, "/epigraph/*");

    RewriteHandler rewriteHandler = new RewriteHandler();
    rewriteHandler.addRule(new RewriteRegexRule("(.*?)/getUser/(.*)", "$1/epigraph/users/$2:record(firstName,lastName)"));
    rewriteHandler.setHandler(contextHandler);

    server.setHandler(rewriteHandler);
    server.start();
    return server;
  }

  @AfterClass
  public static void stop() throws Exception {
    jettyServer.stop();
  }

  @Test
  public void testWithRewrite() throws Exception {
    int portWithRewrite = UNIQUE_PORT.incrementAndGet();
    Server server = startWithRewrite(portWithRewrite);

    try {
      get(HOST, portWithRewrite, "/api/epigraph/users[2]:record(firstName,lastName)",200,"[{\"K\":2,\"V\":{\"firstName\":\"First2\",\"lastName\":\"Last2\"}}]");
      get(HOST, portWithRewrite, "/api/getUser/2",200,"{\"firstName\":\"First2\",\"lastName\":\"Last2\"}");
    } finally {
      server.stop();
    }

  }

  public static class TestServlet extends AsyncEpigraphServlet {
    @Override
    protected @NotNull Service initService(final ServletConfig config) throws ServiceInitializationException {
      return buildUsersService();
    }
  }
}
