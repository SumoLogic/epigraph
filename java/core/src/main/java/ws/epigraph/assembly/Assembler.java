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

package ws.epigraph.assembly;

import org.jetbrains.annotations.NotNull;

/**
 * Base interface for generated data assemblers. Builds Epigraph
 * data instance based on data transfer object and request projection.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@FunctionalInterface
public interface Assembler<D, P, R> {
  /**
   * Assembles Epigraph data instance
   *
   * @param dto        data transfer object
   * @param projection request projection
   * @param ctx        assembly context
   *
   * @return Epigraph data
   */
  @NotNull R assemble(@NotNull D dto, @NotNull P projection, @NotNull AssemblerContext ctx);

  // add `andThen(Function)` if needed
}
