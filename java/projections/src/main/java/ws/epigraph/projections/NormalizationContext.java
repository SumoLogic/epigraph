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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.gen.GenProjectionReference;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.TypeApi;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

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
  private final @NotNull LinkedHashMap<VisitedKey, R> visited = new LinkedHashMap<>();
  private final @NotNull IdentityHashMap<R, R> refToOrigin = new IdentityHashMap<>();
  private final @NotNull Function<T, R> referenceFactory;

  public NormalizationContext(final @NotNull Function<T, R> referenceFactory) {
    this.referenceFactory = referenceFactory;
  }

  @Contract(pure = true)
  public @NotNull LinkedHashMap<VisitedKey, R> visited() { return visited; }

  public @NotNull R newReference(@NotNull T type, @NotNull R origin) {
    R ref = referenceFactory.apply(type);
    refToOrigin.put(ref, origin);
    return ref;
  }

  public @Nullable R origin(@NotNull R ref) { return refToOrigin.get(ref); }

  protected static <
      T extends TypeApi,
      R extends GenProjectionReference<?>,
      C extends NormalizationContext<T, R>,
      G>
  G withContext(
      @NotNull ThreadLocal<C> threadLocal,
      @NotNull Supplier<C> contextFactory,
      @NotNull Function<C, G> function) {

    C ctx = threadLocal.get();
    boolean created = ctx == null;

    if (created) {
      ctx = contextFactory.get();
      threadLocal.set(ctx);
    }

    try {
      return function.apply(ctx);
    } finally {
      if (created)
        threadLocal.remove();
    }
  }

  public static class VisitedKey {
    public final @NotNull ProjectionReferenceName referenceName;
    public final @NotNull TypeName targetTypeName;

    public VisitedKey(
        final @NotNull ProjectionReferenceName referenceName,
        final @NotNull TypeName name) {
      this.referenceName = referenceName;
      targetTypeName = name;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final VisitedKey key = (VisitedKey) o;
      return Objects.equals(referenceName, key.referenceName) &&
             Objects.equals(targetTypeName, key.targetTypeName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(referenceName, targetTypeName);
    }
  }
}
