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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.gen.GenEntityProjection;
import ws.epigraph.types.TypeApi;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class VarNormalizationContext<VP extends GenEntityProjection<VP, ?, ?>>
    extends NormalizationContext<TypeApi, VP> {
  // here we heavily assume that the same thread can't be normalizing two projections of different
  // families at the same time, e.g. that normalizing OpOutput projection can't entail
  // normalizing OpInputProjection
  // see also javadoc comment in NormalizationContext

  private static final ThreadLocal<VarNormalizationContext<?>> tl = new ThreadLocal<>();

  public VarNormalizationContext(final @NotNull Function<TypeApi, VP> referenceFactory) {
    super(referenceFactory);
  }

  @SuppressWarnings("unchecked")
  public static <VP extends GenEntityProjection<VP, ?, ?>, G>
  G withContext(
      @NotNull Supplier<VarNormalizationContext<VP>> contextFactory,
      @NotNull Function<VarNormalizationContext<VP>, G> function) {
    return withContext(
        (ThreadLocal<VarNormalizationContext<VP>>) (Object) tl,
        contextFactory,
        function
    );
  }
}
