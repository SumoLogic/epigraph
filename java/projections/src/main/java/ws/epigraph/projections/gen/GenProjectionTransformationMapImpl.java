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

package ws.epigraph.projections.gen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

/**
 * Projection transformation mapping, from old to new projection nodes
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenProjectionTransformationMapImpl<
    VP extends GenEntityProjection<VP, ?, MP>,
    MP extends GenModelProjection</*MP*/?, /*RMP*/?, /*RMP*/?, /*M*/?>
    > implements GenProjectionTransformationMap<VP, MP> {

  private final @NotNull IdentityHashMap<VP, VP> epMapping = new IdentityHashMap<>();
  private final @NotNull IdentityHashMap<MP, MP> mpMapping = new IdentityHashMap<>();

  public void addEntityMapping(@NotNull VP old, @NotNull VP _new) {
    if (old != _new) epMapping.put(old, _new);
  }

  public void addModelMapping(@NotNull MP old, @NotNull MP _new) {
    if (old != _new) mpMapping.put(old, _new);
  }

  @Override
  public @Nullable VP getEntityMapping(@NotNull VP old) { return epMapping.get(old); }

  @Override
  public @Nullable MP getModelMapping(@NotNull MP old) { return mpMapping.get(old); }

  @Override
  public int size() { return epMapping.size() + mpMapping.size(); }
}
