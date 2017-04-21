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

package ws.epigraph.examples.library;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import ws.epigraph.examples.library.resources.books.BooksResourceDeclaration;
import ws.epigraph.refs.IndexBasedTypesResolver;
import ws.epigraph.server.http.undertow.EpigraphUndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;

import java.util.Collections;

public class LibraryServer {
  public static final int PORT = 8888;
  public static final String HOST = "localhost";
  public static final int TIMEOUT = 100;                    // response timeout in ms

  private static Service buildLibraryService() throws ServiceInitializationException {
    return new Service(
        BooksResourceDeclaration.INSTANCE.fieldName(),      // root field name
        Collections.singleton(                              // collection of resources
            new BooksResourceFactory().getBooksResource()
        )
    );
  }

  public static void main(String[] args) throws ServiceInitializationException {
    Undertow server = Undertow.builder()
        .addHttpListener(PORT, HOST)
        .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
        .setHandler(
            new EpigraphUndertowHandler(
                buildLibraryService(),
                IndexBasedTypesResolver.INSTANCE,
                TIMEOUT
            )
        )
        .build();

    server.start();
  }
}
