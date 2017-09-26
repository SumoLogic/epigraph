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
import ws.epigraph.projections.op.OpProjectionTransformer;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpRequiredSynchronizer extends OpProjectionTransformer {
  // logic here is similar to ReqRequiredSynchronizer, keep them in sync todo extract common code

  private final @NotNull PsiProcessingContext context;

  private final Map<OpOutputModelProjection<?, ?, ?, ?>, EntityProjectionAndDataType> modelToEntity =
      new IdentityHashMap<>();

  public OpRequiredSynchronizer(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected @NotNull OpOutputVarProjection transformResolved(
      final @NotNull OpOutputVarProjection projection,
      final @Nullable DataTypeApi dataType) {

    // build model -> entity index
    for (final Map.Entry<String, OpOutputTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
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

    // todo should we also mark entity as required if self- or retro- tag is marked as requried?

    return super.transformResolved(projection, dataType);
  }

  private boolean flagModel(@NotNull OpOutputModelProjection<?, ?, ?, ?> modelProjection) {
    if (modelProjection.flagged()) return false;
    else {
      EntityProjectionAndDataType epd = modelToEntity.get(modelProjection);
      if (epd == null || !epd.ep.flagged()) return false;
      else {
        OpOutputVarProjection entityProjection = epd.ep;
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
  protected @NotNull OpOutputRecordModelProjection transformRecordModelProjection(
      final @NotNull OpOutputRecordModelProjection recordModelProjection,
      final @NotNull Map<String, OpOutputFieldProjectionEntry> transformedFields,
      final @Nullable List<OpOutputRecordModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(recordModelProjection)) {
      OpOutputRecordModelProjection newProjection = new OpOutputRecordModelProjection(
          recordModelProjection.type(),
          true,
          recordModelProjection.defaultValue(),
          recordModelProjection.params(),
          recordModelProjection.annotations(),
          transformedMeta,
          transformedFields,
          transformedTails,
          recordModelProjection.location()
      );

      fixTransformedModel(recordModelProjection, newProjection);
      return newProjection;
    } else return super.transformRecordModelProjection(
        recordModelProjection,
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpOutputMapModelProjection transformMapModelProjection(
      final @NotNull OpOutputMapModelProjection mapModelProjection,
      final @NotNull OpOutputVarProjection transformedItemsProjection,
      final @Nullable List<OpOutputMapModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(mapModelProjection)) {
      OpOutputMapModelProjection newProjection = new OpOutputMapModelProjection(
          mapModelProjection.type(),
          true,
          mapModelProjection.defaultValue(),
          mapModelProjection.params(),
          mapModelProjection.annotations(),
          transformedMeta,
          mapModelProjection.keyProjection(), // transform in base class too?
          transformedItemsProjection,
          transformedTails,
          mapModelProjection.location()
      );

      fixTransformedModel(mapModelProjection, newProjection);
      return newProjection;
    } else return super.transformMapModelProjection(
        mapModelProjection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpOutputListModelProjection transformListModelProjection(
      final @NotNull OpOutputListModelProjection listModelProjection,
      final @NotNull OpOutputVarProjection transformedItemsProjection,
      final @Nullable List<OpOutputListModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(listModelProjection)) {
      OpOutputListModelProjection newProjection = new OpOutputListModelProjection(
          listModelProjection.type(),
          true,
          listModelProjection.defaultValue(),
          listModelProjection.params(),
          listModelProjection.annotations(),
          transformedMeta,
          transformedItemsProjection,
          transformedTails,
          listModelProjection.location()
      );

      fixTransformedModel(listModelProjection, newProjection);
      return newProjection;
    } else return super.transformListModelProjection(
        listModelProjection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  @Override
  protected @NotNull OpOutputPrimitiveModelProjection transformPrimitiveModelProjection(
      final @NotNull OpOutputPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<OpOutputPrimitiveModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(primitiveModelProjection)) {
      OpOutputPrimitiveModelProjection newProjection = new OpOutputPrimitiveModelProjection(
          primitiveModelProjection.type(),
          true,
          primitiveModelProjection.defaultValue(),
          primitiveModelProjection.params(),
          primitiveModelProjection.annotations(),
          transformedMeta,
          transformedTails,
          primitiveModelProjection.location()
      );

      fixTransformedModel(primitiveModelProjection, newProjection);
      return newProjection;
    } else return super.transformPrimitiveModelProjection(
        primitiveModelProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  private static final class EntityProjectionAndDataType {
    public final @NotNull OpOutputVarProjection ep;
    public final @Nullable DataTypeApi dataType;

    private EntityProjectionAndDataType(
        final @NotNull OpOutputVarProjection ep,
        final @Nullable DataTypeApi type) {

      this.ep = ep;
      dataType = type;
    }
  }
}
