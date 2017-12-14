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

package ws.epigraph.url.projections.req.postprocess;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.req.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;

import java.util.List;
import java.util.Map;

/**
 * Transformer for synchronizing 'required' flags between entity and model projections, more specifically:
 * <ul>
 * <li>Checks that required (+) entity projections are either self-vars or have a retro tag</li>
 * <li>If the above test passes: marks all entity models as required</li>
 * </ul>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqRequiredSynchronizer extends ReqModelEntityTrackingTransformer {

  protected final @NotNull MessagesContext context;


  public ReqRequiredSynchronizer(final @NotNull MessagesContext context) {this.context = context;}

  @Override
  protected @NotNull ReqEntityProjection transformResolvedEntityProjection(
      final @NotNull ReqEntityProjection projection,
      final @Nullable DataTypeApi dataType,
      final @Nullable List<ReqEntityProjection> transformedTails,
      final boolean tailsChanged) {

    // check if flag is valid
    if (projection.flag()
        && dataType != null
        && dataType.retroTag() == null) {

      context.addError(
          String.format(
              "Entity projection is marked as required, but type '%s' has no retro tag defined",
              dataType.name().toString()
          ),
          projection.location()
      );
    }

    return super.transformResolvedEntityProjection(projection, dataType, transformedTails, tailsChanged);
  }

  @Override
  protected ReqEntityProjection transformEntityProjection(
      final @NotNull ReqEntityProjection entityProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, ReqTagProjectionEntry> transformedTagProjections,
      final @Nullable List<ReqEntityProjection> transformedTails,
      final boolean mustRebuild) {

    TagApi retroTag = dataType == null ? null : dataType.retroTag();
    // nullable here is legit but breaks JaCoCo: http://forge.ow2.org/tracker/?func=detail&aid=317789&group_id=23&atid=100023
    /*@Nullable*/
    ReqTagProjectionEntry tp = null;
    if (retroTag != null)
      tp = entityProjection.tagProjection(retroTag.name());

    // this is either a self-var and model projection is flagged
    // or we have a retro tag and it's model is flagged
    boolean modelFlagged = tp != null && tp.modelProjection().flag();

    return super.transformEntityProjection(
        entityProjection,
        entityProjection.flag() || modelFlagged,
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }

  @Override
  protected @NotNull ReqRecordModelProjection transformRecordProjection(
      final @NotNull ReqRecordModelProjection recordModelProjection,
      final @NotNull Map<String, ReqFieldProjectionEntry> transformedFields,
      final @Nullable List<ReqRecordModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformRecordProjection(
        recordModelProjection,
        recordModelProjection.flag() || flagModel(recordModelProjection),
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqMapModelProjection transformMapProjection(
      final @NotNull ReqMapModelProjection mapModelProjection,
      final @NotNull ReqProjection<?, ?> transformedItemsProjection,
      final @Nullable List<ReqMapModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformMapProjection(
        mapModelProjection,
        mapModelProjection.flag() || flagModel(mapModelProjection),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqListModelProjection transformListProjection(
      final @NotNull ReqListModelProjection listModelProjection,
      final @NotNull ReqProjection<?, ?> transformedItemsProjection,
      final @Nullable List<ReqListModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformListProjection(
        listModelProjection,
        listModelProjection.flag() || flagModel(listModelProjection),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull ReqPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqPrimitiveModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformPrimitiveProjection(
        primitiveModelProjection,
        primitiveModelProjection.flag() || flagModel(primitiveModelProjection),
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

}
