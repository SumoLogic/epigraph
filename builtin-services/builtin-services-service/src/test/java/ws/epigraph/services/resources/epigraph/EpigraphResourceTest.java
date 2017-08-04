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

package ws.epigraph.services.resources.epigraph;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.server.http.undertow.EpigraphUndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.services._resources.epigraph.EpigraphResourceDeclaration;
import ws.epigraph.types.Type;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EpigraphResourceTest {
  private static final int PORT = 8888;
  private static final String HOST = "localhost";
  private static final int TIMEOUT = 100; // ms

  private static final StaticTypesResolver resolver = StaticTypesResolver.instance();
  private static Undertow server;

  private static @NotNull Service buildEpigraphService(Map<String, ? extends Type> types)
      throws ServiceInitializationException {
    return new Service(
        EpigraphResourceDeclaration.INSTANCE.fieldName(),
        Collections.singleton(
            new EpigraphResourceFactory(types).getEpigraphResource()
        )
    );
  }

  @BeforeClass
  public static void start() throws ServiceInitializationException {
    server = Undertow.builder()
        .addHttpListener(PORT, HOST)
        .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
        .setHandler(new EpigraphUndertowHandler(
            buildEpigraphService(resolver.types()),
            resolver,
            TIMEOUT))
        .build();

    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop();
  }

  public static void main(String[] args) throws ServiceInitializationException {
    start();
  }

}
