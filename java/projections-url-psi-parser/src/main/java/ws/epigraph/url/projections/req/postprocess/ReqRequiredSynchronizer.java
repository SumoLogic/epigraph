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
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;
import ws.epigraph.url.projections.req.ReqProjectionTransformer;

import java.util.IdentityHashMap;
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
public class ReqRequiredSynchronizer extends ReqProjectionTransformer {
  // logic here is similar to OpRequiredSynchronizer, keep them in sync todo extract common code

  protected final @NotNull PsiProcessingContext context;

  private final Map<ReqModelProjection<?, ?, ?>, EntityProjectionAndDataType> modelToEntity =
      new IdentityHashMap<>();

  public ReqRequiredSynchronizer(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected @NotNull ReqEntityProjection transformResolved(
      @NotNull ReqEntityProjection projection,
      @Nullable DataTypeApi dataType) {

    // build model -> entity index
    for (final Map.Entry<String, ReqTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
      modelToEntity.put(entry.getValue().projection(), new EntityProjectionAndDataType(projection, dataType));
    }

    // check if flag is valid
    if (projection.flagged()
        && projection.type().kind() != TypeKind.ENTITY
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

    return super.transformResolved(projection, dataType);
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
    else if (entityProjection.type().kind() != TypeKind.ENTITY)
      tp = entityProjection.singleTagProjection();

    // this is either a self-var and model projection is flagged
    // or we have a retro tag and it's model is flagged
    boolean modelFlagged = tp != null && tp.projection().flagged();

    return super.transformEntityProjection(
        entityProjection,
        entityProjection.flagged() || modelFlagged,
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }

  protected boolean flagModel(@NotNull ReqModelProjection<?, ?, ?> modelProjection) {
    if (modelProjection.flagged()) return false;
    else {
      EntityProjectionAndDataType epd = modelToEntity.get(modelProjection);
      if (epd == null || !epd.ep.flagged()) return false;
      else {
        ReqEntityProjection entityProjection = epd.ep;
        DataTypeApi dataType = epd.dataType;

        if (entityProjection.type().kind() != TypeKind.ENTITY) // we're the '$self' model
          return true;
        else if (dataType == null) // nothing known about entity container type, can only guess
          return false;
        else {
          TagApi retroTag = dataType.retroTag();
          return retroTag != null && retroTag.type().equals(modelProjection.type());
        }
      }
    }
  }

  @Override
  protected @NotNull ReqRecordModelProjection transformRecordModelProjection(
      final @NotNull ReqRecordModelProjection recordModelProjection,
      final @NotNull Map<String, ReqFieldProjectionEntry> transformedFields,
      final @Nullable List<ReqRecordModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformRecordModelProjection(
        recordModelProjection,
        recordModelProjection.flagged() || flagModel(recordModelProjection),
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqMapModelProjection transformMapModelProjection(
      final @NotNull ReqMapModelProjection mapModelProjection,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqMapModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformMapModelProjection(
        mapModelProjection,
        mapModelProjection.flagged() || flagModel(mapModelProjection),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqListModelProjection transformListModelProjection(
      final @NotNull ReqListModelProjection listModelProjection,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqListModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformListModelProjection(
        listModelProjection,
        listModelProjection.flagged() || flagModel(listModelProjection),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  @Override
  protected @NotNull ReqPrimitiveModelProjection transformPrimitiveModelProjection(
      final @NotNull ReqPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqPrimitiveModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformPrimitiveModelProjection(
        primitiveModelProjection,
        primitiveModelProjection.flagged() || flagModel(primitiveModelProjection),
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  private static final class EntityProjectionAndDataType {
    public final @NotNull ReqEntityProjection ep;
    public final @Nullable DataTypeApi dataType;

    private EntityProjectionAndDataType(
        final @NotNull ReqEntityProjection ep,
        final @Nullable DataTypeApi type) {

      this.ep = ep;
      dataType = type;
    }
  }
}
