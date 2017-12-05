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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.projections.op.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class MarkLeafsAsDeletableTransformer extends OpProjectionTransformer {
  // model projections should be skipped if they are used as entity tag projections
  private final Set<OpPrimitiveModelProjection> skip = Collections.newSetFromMap(new IdentityHashMap<>());

  public MarkLeafsAsDeletableTransformer() { }

  @Override
  protected @NotNull OpEntityProjection transformResolvedEntityProjection(
      final @NotNull OpEntityProjection projection,
      final @Nullable DataTypeApi dataType,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean tailsChanged) {

    skip.addAll(
        projection.tagProjections().values()
            .stream()
            .map(AbstractTagProjectionEntry::modelProjection)
            .filter(mp -> mp.type().kind() == TypeKind.PRIMITIVE)
            .map(mp -> (OpPrimitiveModelProjection) mp)
            .collect(Collectors.toList())
    );
    return super.transformResolvedEntityProjection(projection, dataType, transformedTails, tailsChanged);
  }

  @Override
  protected OpEntityProjection transformEntityProjection(
      final @NotNull OpEntityProjection entityProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, OpTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean mustRebuild) {

    boolean markDeletable = false;

    if (dataType != null) {
      TagApi retroTag = dataType.retroTag();

      if (retroTag != null) {

        // questionable heuristic to keep things compatible when introducing retro tags:
        // mark entity projection as deletable if it's only tag is a retro tag

        OpTagProjectionEntry singleTagProjection = entityProjection.singleTagProjection();
        markDeletable = singleTagProjection != null && singleTagProjection.tag().equals(retroTag);
      }
    }

    return transformEntityProjection(
        entityProjection,
        entityProjection.flag() || markDeletable,
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull OpPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<OpPrimitiveModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    boolean markDeletable = !skip.contains(primitiveModelProjection);

    return transformPrimitiveProjection(
        primitiveModelProjection,
        markDeletable,
        transformedTails,
        transformedMeta,
        mustRebuild || (markDeletable && !primitiveModelProjection.flag())
    );
  }
}
