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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.wire.FormatException;
import ws.epigraph.wire.FormatFactories;

import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class NameBasedFormatSelector<C extends HttpInvocationContext> implements FormatSelector<C> {
  private final @NotNull Map<String, FormatFactories> nameToFactories;
  private final @NotNull Function<C, String> formatNameExtractor;
  private final @NotNull FormatFactories defaultFactories;

  public NameBasedFormatSelector(
      final @NotNull Map<String, FormatFactories> nameToFactories,
      final @NotNull Function<C, String> formatNameExtractor,
      final @NotNull FormatFactories defaultFactories) {

    this.nameToFactories = nameToFactories;
    this.formatNameExtractor = formatNameExtractor;
    this.defaultFactories = defaultFactories;
  }

  @Override
  public @NotNull FormatFactories getFactories(final @NotNull C context) throws FormatException {
    String formatName = formatNameExtractor.apply(context);
    if (formatName == null)
      return defaultFactories;

    FormatFactories factories = nameToFactories.get(formatName);
    if (factories == null)
      throw new FormatException("'" + formatName + "' format is not supported");

    return factories;
  }
}
