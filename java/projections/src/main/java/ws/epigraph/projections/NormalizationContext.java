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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.gen.GenProjectionReference;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.TypeApi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Projection normalization context, keeps track of visited projections and
 * allows to normalize recursive ones
 * <p/>
 * We need a way to pass normalization context around. I tried passing it explicitly, but
 * it badly pollutes all the signatures, gets especially bad around `GenModelProjection.normalizeForType`
 * which has to handle it too, but has no `VP` type parameter and in general should not be concerned.
 * <p/>
 * Currently thread locals in (see subclasses) are used to keep context instance.
 * I don't like thread locals but this looks like a reasonable compromise for now. Should be changed to
 * explicit parameter in case of any problems.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class NormalizationContext<T extends TypeApi, R extends GenProjectionReference<?>> {
  private final @NotNull Map<ProjectionReferenceName, R> visited = new HashMap<>();
  private final @NotNull Function<T, R> referenceFactory;

  public NormalizationContext(final @NotNull Function<T, R> referenceFactory) {
    this.referenceFactory = referenceFactory;
  }

  @Contract(pure = true)
  public @NotNull Map<ProjectionReferenceName, R> visited() { return visited; }

  public @NotNull R newReference(@NotNull T type) { return referenceFactory.apply(type); }

  protected static <
      T extends TypeApi,
      R extends GenProjectionReference<?>,
      C extends NormalizationContext<T, R>,
      G>
  G withContext(
      @NotNull ThreadLocal<C> threadLocal,
      @NotNull Function0<C> contextFactory,
      @NotNull Function<C, G> function) {

    C ctx = threadLocal.get();
    boolean created = ctx == null;

    if (created) {
      ctx = contextFactory.apply();
      threadLocal.set(ctx);
    }

    try {
      return function.apply(ctx);
    } finally {
      if (created)
        threadLocal.remove();
    }
  }

  public interface Function0<R> {
    R apply();
  }
}
