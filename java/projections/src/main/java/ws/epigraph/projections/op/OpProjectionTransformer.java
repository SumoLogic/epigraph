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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenProjectionTransformer;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpProjectionTransformer extends GenProjectionTransformer<
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    OpRecordModelProjection,
    OpMapModelProjection,
    OpListModelProjection,
    OpPrimitiveModelProjection,
    OpFieldProjectionEntry,
    OpFieldProjection,
    OpProjectionTransformationMap,
    OpProjectionTransformationMapImpl
    > {

  // NB keep in sync with ReqProjectionTransformer

  @Override
  protected OpEntityProjection transformEntityProjection(
      final @NotNull OpEntityProjection entityProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, OpTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean mustRebuild) {

    return transformEntityProjection(
        entityProjection,
        entityProjection.flag(),
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );

  }

  protected final OpEntityProjection transformEntityProjection(
      final @NotNull OpEntityProjection entityProjection,
      final boolean flagOverride,
      final @NotNull Map<String, OpTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpEntityProjection> transformedTails,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != entityProjection.flag()) {
      OpEntityProjection newProjection = new OpEntityProjection(
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
  protected @NotNull OpTagProjectionEntry transformTagProjection(
      final @NotNull OpEntityProjection entityProjection,
      final @NotNull TagApi tag,
      final @NotNull OpTagProjectionEntry tagProjection,
      final @NotNull OpModelProjection<?, ?, ?, ?> transformedModelProjection,
      final boolean mustRebuild) {

    return mustRebuild ?
           new OpTagProjectionEntry(
               tag,
               transformedModelProjection,
               tagProjection.location()
           ) : tagProjection;
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
        recordModelProjection.flag(),
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild
    );

  }

  protected final @NotNull OpRecordModelProjection transformRecordProjection(
      final @NotNull OpRecordModelProjection recordModelProjection,
      final boolean flagOverride,
      final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
      final @Nullable List<OpRecordModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != recordModelProjection.flag()) {
      OpRecordModelProjection newProjection = new OpRecordModelProjection(
          recordModelProjection.type(),
          flagOverride,
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
    } else return recordModelProjection;
  }

  @Override
  protected @NotNull OpFieldProjection transformFieldProjection(
      final @NotNull OpFieldProjection fieldProjection,
      final @NotNull OpEntityProjection transformedEntityProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new OpFieldProjection(
        transformedEntityProjection,
        fieldProjection.location()
    ) : fieldProjection;
  }

  @Override
  protected @NotNull OpFieldProjectionEntry transformFieldProjectionEntry(
      final @NotNull OpRecordModelProjection modelProjection,
      final @NotNull OpFieldProjectionEntry fieldProjectionEntry,
      final @NotNull OpFieldProjection transformedFieldProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new OpFieldProjectionEntry(
        fieldProjectionEntry.field(),
        transformedFieldProjection,
        fieldProjectionEntry.location()
    ) : fieldProjectionEntry;
  }

  @Override
  protected @NotNull OpMapModelProjection transformMapProjection(
      final @NotNull OpMapModelProjection mapModelProjection,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpMapModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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

  protected final @NotNull OpMapModelProjection transformMapProjection(
      final @NotNull OpMapModelProjection mapModelProjection,
      final boolean flagOverride,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpMapModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != mapModelProjection.flag()) {
      OpMapModelProjection newProjection = new OpMapModelProjection(
          mapModelProjection.type(),
          flagOverride,
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
    } else return mapModelProjection;
  }

  @Override
  protected @NotNull OpListModelProjection transformListProjection(
      final @NotNull OpListModelProjection listModelProjection,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpListModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
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

  protected final @NotNull OpListModelProjection transformListProjection(
      final @NotNull OpListModelProjection listModelProjection,
      final boolean flagOverride,
      final @NotNull OpEntityProjection transformedItemsProjection,
      final @Nullable List<OpListModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != listModelProjection.flag()) {
      OpListModelProjection newProjection = new OpListModelProjection(
          listModelProjection.type(),
          flagOverride,
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
    } else return listModelProjection;
  }

  @Override
  protected @NotNull OpPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull OpPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<OpPrimitiveModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    return transformPrimitiveProjection(
        primitiveModelProjection,
        primitiveModelProjection.flag(),
        transformedTails,
        transformedMeta,
        mustRebuild
    );
  }

  protected final @NotNull OpPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull OpPrimitiveModelProjection primitiveModelProjection,
      final boolean flagOverride,
      final @Nullable List<OpPrimitiveModelProjection> transformedTails,
      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild || flagOverride != primitiveModelProjection.flag()) {
      OpPrimitiveModelProjection newProjection = new OpPrimitiveModelProjection(
          primitiveModelProjection.type(),
          flagOverride,
          primitiveModelProjection.defaultValue(),
          primitiveModelProjection.params(),
          primitiveModelProjection.annotations(),
          transformedMeta,
          transformedTails,
          primitiveModelProjection.location()
      );

      fixTransformedModel(primitiveModelProjection, newProjection);
      return newProjection;
    } else return primitiveModelProjection;
  }

  protected final void fixTransformedEntity(@NotNull OpEntityProjection old, @NotNull OpEntityProjection _new) {
    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames(old);
  }

  @SuppressWarnings("unchecked")
  protected <SMP extends OpModelProjection<?, SMP, ?, ?>> void fixTransformedModel(
      @NotNull OpModelProjection<?, ?, ?, ?> old,
      @NotNull OpModelProjection<?, SMP, ?, ?> _new) {

    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames((SMP) old);
  }

  @Override
  protected @NotNull OpEntityProjection newEntityRef(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new OpEntityProjection(type, location);
  }

  @Override
  protected @NotNull OpModelProjection<?, ?, ?, ?> newModelRef(
      final @NotNull DatumTypeApi model,
      final @NotNull TextLocation location) {
    switch (model.kind()) {
      case RECORD:
        return new OpRecordModelProjection((RecordTypeApi) model, location);
      case MAP:
        return new OpMapModelProjection((MapTypeApi) model, location);
      case LIST:
        return new OpListModelProjection((ListTypeApi) model, location);
      case PRIMITIVE:
        return new OpPrimitiveModelProjection((PrimitiveTypeApi) model, location);
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + model.kind());
    }
  }

  @Override
  protected @NotNull OpProjectionTransformationMapImpl newTransformationMap() {
    return new OpProjectionTransformationMapImpl();
  }
}
