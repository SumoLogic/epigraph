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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.op.*;
import ws.epigraph.types.DataTypeApi;

import java.util.List;
import java.util.Map;

/**
 * Populates 'include in default' flag from marked node all the way to the root
 * <p>
 * For example<br/>
 * <code>(firstName, bestFriend:(id, record(+lastName)))</code><br/>
 * becomes<br/>
 * <code>+(firstName, +bestFriend:(id, +record(+lastName)))</code>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DefaultsPopulatingTransformer extends OpProjectionTransformer {
  // todo unused, delete (since '+' semantics is reversed now)

  public DefaultsPopulatingTransformer() {}

  @Override
  protected OpEntityProjection transformEntityProjection(
      final @NotNull OpEntityProjection entityProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, OpTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean mustRebuild) {

    boolean newFlag = entityProjection.flag() ||
                      transformedTagProjections.values()
                          .stream()
                          .anyMatch(tpe -> tpe.modelProjection().flag());

    return super.transformEntityProjection(
        entityProjection,
        newFlag,
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpRecordModelProjection transformRecordProjection(
      final @NotNull OpRecordModelProjection recordModelProjection,
      final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
      final @Nullable List<OpRecordModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    boolean newFlag = recordModelProjection.flag() ||
                      transformedFields.values()
                          .stream()
                          .anyMatch(fpe -> fpe.fieldProjection().projection().flag());

    return super.transformRecordProjection(
        recordModelProjection,
        newFlag,
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpMapModelProjection transformMapProjection(
      final @NotNull OpMapModelProjection mapModelProjection,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpMapModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return super.transformMapProjection(
        mapModelProjection,
        mapModelProjection.flag() || transformedItemsProjection.flag(),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpListModelProjection transformListProjection(
      final @NotNull OpListModelProjection listModelProjection,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpListModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return super.transformListProjection(
        listModelProjection,
        listModelProjection.flag() || transformedItemsProjection.flag(),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }
}
