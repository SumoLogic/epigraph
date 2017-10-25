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

import java.util.function.Function;

/**
 * Base interface for generated data assemblers. Builds Epigraph
 * data instance based on data transfer object and request projection.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@FunctionalInterface
public interface Asm<D, P, R> {
  /**
   * Assembles Epigraph data instance
   *
   * @param dto        data transfer object
   * @param projection request projection
   * @param ctx        assembly context
   *
   * @return Epigraph data
   */
  @NotNull R assemble(@NotNull D dto, @NotNull P projection, @NotNull AsmContext ctx);

  /**
   * Composes a function {@code f} with an assembler by applying {@code f} to the data object
   * before {@code assemble} call. Ideologically it would be similar to {@code f.andThen(this)}
   *
   * @param f   function to run on the data object
   * @param <T> new data object type
   *
   * @return composed assembler
   */
  default <T> @NotNull Asm<T, P, R> on(@NotNull Function<T, D> f) {
    return (dto, projection, ctx) -> assemble(f.apply(dto), projection, ctx);
  }

  /**
   * Composes a function {@code f} with an assembler by applying {@code f} to the data object
   * before {@code assemble} call. Ideologically it would be similar to {@code f.andThen(this)}
   *
   * @param f   function to run on the data object
   * @param ef  function to be called if {@code f} application produces an error
   * @param <T> new data object type
   *
   * @return composed assembler
   */
  default <T> @NotNull Asm<T, P, R> on(@NotNull Function<T, D> f, @NotNull Function<RuntimeException, R> ef) {
    return (dto, projection, ctx) -> {
      D d;
      try {
        d = f.apply(dto);
      } catch (RuntimeException ex) {
        return ef.apply(ex);
      }
      return assemble(d, projection, ctx);
    };
  }

  /**
   * Composes a function {@code f} with an assembler by applying {@code f} to the result object
   * after {@code assemble} call.
   *
   * @param f   function to run on the assembly result
   * @param <T> new assembly result type
   *
   * @return composed assembler
   */
  default <T> @NotNull Asm<D, P, T> andThen(@NotNull Function<R, T> f) {
    return (dto, projection, ctx) -> f.apply(assemble(dto, projection, ctx));
  }
}
