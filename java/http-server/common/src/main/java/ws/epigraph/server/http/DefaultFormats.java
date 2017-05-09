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

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.wire.FormatFactories;
import ws.epigraph.wire.json.JsonFormatFactories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Default Format selector based on static map of format names to format factories
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@ThreadSafe
public final class DefaultFormats {
  public static final String JSON_FORMAT_NAME = "json";

  private static final Map<String, FormatFactories> DEFAULT_FACTORIES;

  static {
    DEFAULT_FACTORIES = new ConcurrentHashMap<>();
    DEFAULT_FACTORIES.put(JSON_FORMAT_NAME, JsonFormatFactories.INSTANCE);
    // add more here as needed
  }

  private DefaultFormats() {}

  /**
   * Gets format selector given format name extractor
   *
   * @param formatNameExtractor function for extracting format name from http invocation context, must be thread safe
   * @param <C>                 invocation context
   *
   * @return {@code FormatSelector} instance
   */
  public static <C extends HttpInvocationContext> @NotNull FormatSelector<C> instance(Function<C, String> formatNameExtractor) {
    return new NameBasedFormatSelector<>(
        DEFAULT_FACTORIES,
        formatNameExtractor,
        JsonFormatFactories.INSTANCE
    );
  }
}
