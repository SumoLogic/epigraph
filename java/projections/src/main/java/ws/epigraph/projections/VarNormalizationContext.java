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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.TypeApi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class VarNormalizationContext<VP extends GenVarProjection<VP, ?, ?>> {
  private final @NotNull Map<Qn, VP> visited = new HashMap<>();
  private final @NotNull Function<TypeApi, VP> referenceFactory;

  public VarNormalizationContext(final @NotNull Function<TypeApi, VP> referenceFactory) {
    this.referenceFactory = referenceFactory;
  }

  @Contract(pure = true)
  public @NotNull Map<Qn, VP> visited() { return visited; }

  public @NotNull VP newReference(@NotNull TypeApi type) { return referenceFactory.apply(type); }
}
