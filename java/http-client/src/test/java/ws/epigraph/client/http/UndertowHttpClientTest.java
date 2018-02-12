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

package ws.epigraph.client.http;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ws.epigraph.server.http.undertow.EpigraphUndertowHandler;
import ws.epigraph.service.ServiceInitializationException;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UndertowHttpClientTest extends AbstractHttpClientTest {
  private static Undertow server;

  private static final int port = UNIQUE_PORT.incrementAndGet();

  @Override
  protected int port() { return port; }

  @BeforeClass
  public static void start() throws ServiceInitializationException {
    server = Undertow.builder()
        .addHttpListener(port, HOST)
        .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
        .setServerOption(UndertowOptions.REQUEST_PARSE_TIMEOUT, 100)
        .setHandler(new EpigraphUndertowHandler(buildUsersService(), TIMEOUT))
        .build();

    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop();
  }
}
