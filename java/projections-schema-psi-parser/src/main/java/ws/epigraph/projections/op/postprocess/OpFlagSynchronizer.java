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

package ws.epigraph.projections.op.postprocess;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.op.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Synchronizes 'flag' between model and entity projections for entity types with retro tags.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFlagSynchronizer extends OpProjectionTransformer {
  // logic here is similar to ReqRequiredSynchronizer, keep them in sync todo extract common code

  private final @NotNull MessagesContext context;

  private final Map<OpModelProjection<?, ?, ?, ?>, EntityProjectionAndDataType> modelToEntity =
      new IdentityHashMap<>();

  private final String flagSemantics;

  public OpFlagSynchronizer(final String flagSemantics, final @NotNull MessagesContext context) {
    this.flagSemantics = flagSemantics;
    this.context = context;
  }

  @Override
  protected @NotNull OpEntityProjection transformResolvedEntityProjection(
      final @NotNull OpEntityProjection projection,
      final @Nullable DataTypeApi dataType,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean tailsChanged) {

    // build model -> entity index
    for (final Map.Entry<String, OpTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
      modelToEntity.put(entry.getValue().modelProjection(), new EntityProjectionAndDataType(projection, dataType));
    }

    return super.transformResolvedEntityProjection(projection, dataType, transformedTails, tailsChanged);
  }

  private boolean flagModel(@NotNull OpModelProjection<?, ?, ?, ?> modelProjection) {
    if (modelProjection.flag()) return false;
    else {
      EntityProjectionAndDataType epd = modelToEntity.get(modelProjection);
      if (epd == null || !epd.ep.flag()) return false;
      else {
        OpEntityProjection entityProjection = epd.ep;
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
  protected @NotNull OpRecordModelProjection transformRecordProjection(
      final @NotNull OpRecordModelProjection recordModelProjection,
      final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
      final @Nullable List<OpRecordModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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
  protected @NotNull OpMapModelProjection transformMapProjection(
      final @NotNull OpMapModelProjection mapModelProjection,
      final @NotNull OpProjection<?, ?> transformedItemsProjection,
      final @Nullable List<OpMapModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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
  protected @NotNull OpListModelProjection transformListProjection(
      final @NotNull OpListModelProjection listModelProjection,
      final @NotNull OpProjection<?, ?> transformedItemsProjection,
      final @Nullable List<OpListModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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
  protected @NotNull OpPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull OpPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<OpPrimitiveModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformPrimitiveProjection(
        primitiveModelProjection,
        primitiveModelProjection.flag() || flagModel(primitiveModelProjection),
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  private static final class EntityProjectionAndDataType {
    public final @NotNull OpEntityProjection ep;
    public final @Nullable DataTypeApi dataType;

    private EntityProjectionAndDataType(
        final @NotNull OpEntityProjection ep,
        final @Nullable DataTypeApi type) {

      this.ep = ep;
      dataType = type;
    }
  }
}
