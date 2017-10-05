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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenProjectionTransformer;
import ws.epigraph.projections.req.*;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqProjectionTransformer extends GenProjectionTransformer<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqMapModelProjection,
    ReqListModelProjection,
    ReqPrimitiveModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection,
    ReqProjectionTransformationMap,
    ReqProjectionTransformationMapImpl
    > {

  // NB keep in sync with OpProjectionTransformer

  @Override
  protected ReqEntityProjection transformEntityProjection(
      final @NotNull ReqEntityProjection entityProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, ReqTagProjectionEntry> transformedTagProjections,
      final @Nullable List<ReqEntityProjection> transformedTails,
      final boolean mustRebuild) {

    return transformEntityProjection(
        entityProjection,
        entityProjection.flag(),
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }

  protected final ReqEntityProjection transformEntityProjection(
      final @NotNull ReqEntityProjection entityProjection,
      final boolean flagOverride,
      final @NotNull Map<String, ReqTagProjectionEntry> transformedTagProjections,
      final @Nullable List<ReqEntityProjection> transformedTails,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != entityProjection.flag()) {
      ReqEntityProjection newProjection = new ReqEntityProjection(
          entityProjection.type(),
          flagOverride,
          transformedTagProjections,
          entityProjection.parenthesized(),
          transformedTails,
          entityProjection.location()
      );

      fixTransformedEntity(entityProjection, newProjection);
      return newProjection;
    } else return entityProjection;
  }

  @Override
  protected @NotNull ReqTagProjectionEntry transformTagProjection(
      final @NotNull ReqEntityProjection entityProjection,
      final @NotNull TagApi tag,
      final @NotNull ReqTagProjectionEntry tagProjection,
      final @NotNull ReqModelProjection<?, ?, ?> transformedModelProjection,
      final boolean mustRebuild) {

    return mustRebuild ?
           new ReqTagProjectionEntry(
               tag,
               transformedModelProjection,
               tagProjection.location()
           ) : tagProjection;
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
        recordModelProjection.flag(),
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  protected final @NotNull ReqRecordModelProjection transformRecordProjection(
      final @NotNull ReqRecordModelProjection recordModelProjection,
      final boolean flagOverride,
      final @NotNull Map<String, ReqFieldProjectionEntry> transformedFields,
      final @Nullable List<ReqRecordModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != recordModelProjection.flag()) {
      ReqRecordModelProjection newProjection = new ReqRecordModelProjection(
          recordModelProjection.type(),
          flagOverride,
          recordModelProjection.params(),
          recordModelProjection.directives(),
          transformedMeta,
          transformedFields,
          transformedTails,
          recordModelProjection.location()
      );

      fixTransformedModel(recordModelProjection, newProjection);
      return newProjection;
    } else return recordModelProjection;
  }

  @Override
  protected @NotNull ReqFieldProjection transformFieldProjection(
      final @NotNull ReqFieldProjection fieldProjection,
      final @NotNull ReqEntityProjection transformedEntityProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new ReqFieldProjection(
        transformedEntityProjection,
        fieldProjection.location()
    ) : fieldProjection;
  }

  @Override
  protected @NotNull ReqFieldProjectionEntry transformFieldProjectionEntry(
      final @NotNull ReqRecordModelProjection modelProjection,
      final @NotNull ReqFieldProjectionEntry fieldProjectionEntry,
      final @NotNull ReqFieldProjection transformedFieldProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new ReqFieldProjectionEntry(
        fieldProjectionEntry.field(),
        transformedFieldProjection,
        fieldProjectionEntry.location()
    ) : fieldProjectionEntry;
  }

  @Override
  protected @NotNull ReqMapModelProjection transformMapProjection(
      final @NotNull ReqMapModelProjection mapModelProjection,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqMapModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformMapProjection(
        mapModelProjection,
        mapModelProjection.flag(),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  protected final @NotNull ReqMapModelProjection transformMapProjection(
      final @NotNull ReqMapModelProjection mapModelProjection,
      final boolean flagOverride,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqMapModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != mapModelProjection.flag()) {
      ReqMapModelProjection newProjection = new ReqMapModelProjection(
          mapModelProjection.type(),
          flagOverride,
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
    } else return mapModelProjection;
  }

  @Override
  protected @NotNull ReqListModelProjection transformListProjection(
      final @NotNull ReqListModelProjection listModelProjection,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqListModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformListProjection(
        listModelProjection,
        listModelProjection.flag(),
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  protected final @NotNull ReqListModelProjection transformListProjection(
      final @NotNull ReqListModelProjection listModelProjection,
      final boolean flagOverride,
      final @NotNull ReqEntityProjection transformedItemsProjection,
      final @Nullable List<ReqListModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != listModelProjection.flag()) {
      ReqListModelProjection newProjection = new ReqListModelProjection(
          listModelProjection.type(),
          flagOverride,
          listModelProjection.params(),
          listModelProjection.directives(),
          transformedMeta,
          transformedItemsProjection,
          transformedTails,
          listModelProjection.location()
      );

      fixTransformedModel(listModelProjection, newProjection);
      return newProjection;
    } else { return listModelProjection;}

  }

  @Override
  protected @NotNull ReqPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull ReqPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqPrimitiveModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformPrimitiveProjection(
        primitiveModelProjection,
        primitiveModelProjection.flag(),
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  protected final @NotNull ReqPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull ReqPrimitiveModelProjection primitiveModelProjection,
      final boolean flagOverride,
      final @Nullable List<ReqPrimitiveModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != primitiveModelProjection.flag()) {
      ReqPrimitiveModelProjection newProjection = new ReqPrimitiveModelProjection(
          primitiveModelProjection.type(),
          flagOverride,
          primitiveModelProjection.params(),
          primitiveModelProjection.directives(),
          transformedMeta,
          transformedTails,
          primitiveModelProjection.location()
      );

      fixTransformedModel(primitiveModelProjection, newProjection);
      return newProjection;
    } else return primitiveModelProjection;
  }

  protected final void fixTransformedEntity(@NotNull ReqEntityProjection old, @NotNull ReqEntityProjection _new) {
    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames(old);
  }

  @SuppressWarnings("unchecked")
  protected <SMP extends ReqModelProjection<?, SMP, ?>> void fixTransformedModel(
      @NotNull ReqModelProjection<?, ?, ?> old,
      @NotNull ReqModelProjection<?, SMP, ?> _new) {

    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames((SMP) old);
  }

  @Override
  protected @NotNull ReqEntityProjection newEntityRef(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new ReqEntityProjection(type, location);
  }

  @Override
  protected @NotNull ReqModelProjection<?, ?, ?> newModelRef(
      final @NotNull DatumTypeApi model,
      final @NotNull TextLocation location) {
    switch (model.kind()) {
      case RECORD:
        return new ReqRecordModelProjection((RecordTypeApi) model, location);
      case MAP:
        return new ReqMapModelProjection((MapTypeApi) model, location);
      case LIST:
        return new ReqListModelProjection((ListTypeApi) model, location);
      case PRIMITIVE:
        return new ReqPrimitiveModelProjection((PrimitiveTypeApi) model, location);
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + model.kind());
    }
  }

  @Override
  protected @NotNull ReqProjectionTransformationMapImpl newTransformationMap() {
    return new ReqProjectionTransformationMapImpl();
  }
}
