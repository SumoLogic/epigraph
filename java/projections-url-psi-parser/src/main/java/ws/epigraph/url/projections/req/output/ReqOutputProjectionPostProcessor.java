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

package ws.epigraph.url.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeKind;
import ws.epigraph.url.projections.req.ReqProjectionTransformer;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Post-processor for output projections. Does a few things:
 * <ul>
 * <li>Checks that required (+) entity projections are either self-vars or have a retro tag</li>
 * <li>If the above test passes: marks all entity models as required</li>
 * </ul>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputProjectionPostProcessor extends ReqProjectionTransformer {
  // logic here is similar to OpInputProjectionPostProcessor, keep them in sync

  private final @NotNull PsiProcessingContext context;

  private final Map<ReqOutputModelProjection<?, ?, ?>, EntityProjectionAndDataType> modelToEntity =
      new IdentityHashMap<>();

  ReqOutputProjectionPostProcessor(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected @NotNull ReqOutputVarProjection transformResolved(
      @NotNull ReqOutputVarProjection projection,
      @Nullable DataTypeApi dataType) {

    // build model -> entity index
    for (final Map.Entry<String, ReqOutputTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
      modelToEntity.put(entry.getValue().projection(), new EntityProjectionAndDataType(projection, dataType));
    }

    // check if flag is valid
    if (projection.flagged()
        && projection.type().kind() != TypeKind.ENTITY
        && dataType != null
        && dataType.defaultTag() == null) {

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

  private boolean flagModel(@NotNull ReqOutputModelProjection<?, ?, ?> modelProjection) {
    if (modelProjection.flagged()) return false;
    else {
      EntityProjectionAndDataType epd = modelToEntity.get(modelProjection);
      if (epd == null) return false;
      else {
        ReqOutputVarProjection entityProjection = epd.ep;
        DataTypeApi dataType = epd.dataType;

        if (entityProjection.type().kind() != TypeKind.ENTITY) // we're the '$self' model
          return true;
        else if (dataType == null) // nothing known about entity container type, can only guess
          return false;
        else {
          TagApi retroTag = dataType.defaultTag();
          return retroTag != null && retroTag.type().equals(modelProjection.type());
        }
      }
    }
  }

  @Override
  protected @NotNull ReqOutputRecordModelProjection transformRecordModelProjection(
      final @NotNull ReqOutputRecordModelProjection recordModelProjection,
      final @NotNull Map<String, ReqOutputFieldProjectionEntry> transformedFields,
      final @Nullable List<ReqOutputRecordModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(recordModelProjection)) {
      ReqOutputRecordModelProjection newProjection = new ReqOutputRecordModelProjection(
          recordModelProjection.type(),
          true,
          recordModelProjection.params(),
          recordModelProjection.directives(),
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
  protected @NotNull ReqOutputMapModelProjection transformMapModelProjection(
      final @NotNull ReqOutputMapModelProjection mapModelProjection,
      final @NotNull ReqOutputVarProjection transformedItemsProjection,
      final @Nullable List<ReqOutputMapModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(mapModelProjection)) {
      ReqOutputMapModelProjection newProjection = new ReqOutputMapModelProjection(
          mapModelProjection.type(),
          true,
          mapModelProjection.params(),
          mapModelProjection.directives(),
          transformedMeta,
          mapModelProjection.keys(),
          mapModelProjection.keysRequired(),
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
  protected @NotNull ReqOutputListModelProjection transformListModelProjection(
      final @NotNull ReqOutputListModelProjection listModelProjection,
      final @NotNull ReqOutputVarProjection transformedItemsProjection,
      final @Nullable List<ReqOutputListModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(listModelProjection)) {
      ReqOutputListModelProjection newProjection = new ReqOutputListModelProjection(
          listModelProjection.type(),
          true,
          listModelProjection.params(),
          listModelProjection.directives(),
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
  protected @NotNull ReqOutputPrimitiveModelProjection transformPrimitiveModelProjection(
      final @NotNull ReqOutputPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqOutputPrimitiveModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flagModel(primitiveModelProjection)) {
      ReqOutputPrimitiveModelProjection newProjection = new ReqOutputPrimitiveModelProjection(
          primitiveModelProjection.type(),
          true,
          primitiveModelProjection.params(),
          primitiveModelProjection.directives(),
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
    public final @NotNull ReqOutputVarProjection ep;
    public final @Nullable DataTypeApi dataType;

    private EntityProjectionAndDataType(
        final @NotNull ReqOutputVarProjection ep,
        final @Nullable DataTypeApi type) {

      this.ep = ep;
      dataType = type;
    }
  }
}
