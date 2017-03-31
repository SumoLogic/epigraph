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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ModelNormalizationContext<M extends DatumTypeApi, MP extends GenModelProjection<?, ?, ?, ?/*M*/>>
    extends NormalizationContext<M, MP> {
  // here we heavily assume that the same thread can't be normalizing two projections of different
  // families at the same time, e.g. that normalizing OpOutput projection can't entail
  // normalizing OpInputProjection
  // see also javadoc comment in NormalizationContext

  private static final ThreadLocal<ModelNormalizationContext<?, ?>> tl = new ThreadLocal<>();

  public ModelNormalizationContext(final @NotNull Function<M, MP> referenceFactory) {
    super(referenceFactory);
  }

  @SuppressWarnings("unchecked")
  public static <M extends DatumTypeApi, MP extends GenModelProjection<?, ?, ?, ?/*M*/>, G>
  G withContext(
      @NotNull Function0<ModelNormalizationContext<M, MP>> contextFactory,
      @NotNull Function<ModelNormalizationContext<M, MP>, G> function) {
    return withContext(
        (ThreadLocal<ModelNormalizationContext<M, MP>>) (Object) tl,
        contextFactory,
        function
    );
  }
}
