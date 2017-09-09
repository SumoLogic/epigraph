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
import ws.epigraph.projections.op.output.*;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpProjectionTransformer extends GenProjectionTransformer<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputMapModelProjection,
    OpOutputListModelProjection,
    OpOutputPrimitiveModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection
    > {

  // NB keep in sync with OpProjectionTransformer

  @Override
  protected OpOutputVarProjection transformVarProjection(
      final @NotNull OpOutputVarProjection varProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, OpOutputTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpOutputVarProjection> transformedTails,
      final boolean mustRebuild) {

    if (mustRebuild) {
      OpOutputVarProjection newProjection = new OpOutputVarProjection(
          varProjection.type(),
          varProjection.flagged(),
          transformedTagProjections,
          varProjection.parenthesized(),
          transformedTails,
          varProjection.location()
      );

      fixTransformedEntity(varProjection, newProjection);
      return newProjection;
    } else return varProjection;
  }

  @Override
  protected @NotNull OpOutputTagProjectionEntry transformTagProjection(
      final @NotNull OpOutputVarProjection varProjection,
      final @NotNull TagApi tag,
      final @NotNull OpOutputTagProjectionEntry tagProjection,
      final @NotNull OpOutputModelProjection<?, ?, ?, ?> transformedModelProjection,
      final boolean mustRebuild) {

    return mustRebuild ?
           new OpOutputTagProjectionEntry(
               tag,
               transformedModelProjection,
               tagProjection.location()
           ) : tagProjection;
  }

  @Override
  protected @NotNull OpOutputRecordModelProjection transformRecordModelProjection(
      final @NotNull OpOutputRecordModelProjection recordModelProjection,
      final @NotNull Map<String, OpOutputFieldProjectionEntry> transformedFields,
      final @Nullable List<OpOutputRecordModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      OpOutputRecordModelProjection newProjection = new OpOutputRecordModelProjection(
          recordModelProjection.type(),
          recordModelProjection.flagged(),
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
  protected @NotNull OpOutputFieldProjection transformFieldProjection(
      final @NotNull OpOutputFieldProjection fieldProjection,
      final @NotNull OpOutputVarProjection transformedEntityProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new OpOutputFieldProjection(
        transformedEntityProjection,
        fieldProjection.location()
    ) : fieldProjection;
  }

  @Override
  protected @NotNull OpOutputFieldProjectionEntry transformFieldProjectionEntry(
      final @NotNull OpOutputRecordModelProjection modelProjection,
      final @NotNull OpOutputFieldProjectionEntry fieldProjectionEntry,
      final @NotNull OpOutputFieldProjection transformedFieldProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new OpOutputFieldProjectionEntry(
        fieldProjectionEntry.field(),
        transformedFieldProjection,
        fieldProjectionEntry.location()
    ) : fieldProjectionEntry;
  }

  @Override
  protected @NotNull OpOutputMapModelProjection transformMapModelProjection(
      final @NotNull OpOutputMapModelProjection mapModelProjection,
      final @NotNull OpOutputVarProjection transformedItemsProjection,
      final @Nullable List<OpOutputMapModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      OpOutputMapModelProjection newProjection = new OpOutputMapModelProjection(
          mapModelProjection.type(),
          mapModelProjection.flagged(),
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
  protected @NotNull OpOutputListModelProjection transformListModelProjection(
      final @NotNull OpOutputListModelProjection listModelProjection,
      final @NotNull OpOutputVarProjection transformedItemsProjection,
      final @Nullable List<OpOutputListModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      OpOutputListModelProjection newProjection = new OpOutputListModelProjection(
          listModelProjection.type(),
          listModelProjection.flagged(),
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
  protected @NotNull OpOutputPrimitiveModelProjection transformPrimitiveModelProjection(
      final @NotNull OpOutputPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<OpOutputPrimitiveModelProjection> transformedTails,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      OpOutputPrimitiveModelProjection newProjection = new OpOutputPrimitiveModelProjection(
          primitiveModelProjection.type(),
          primitiveModelProjection.flagged(),
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

  protected void fixTransformedEntity(@NotNull OpOutputVarProjection old, @NotNull OpOutputVarProjection _new) {
    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames(old);
  }

  @SuppressWarnings("unchecked")
  protected <SMP extends OpOutputModelProjection<?, SMP, ?, ?>> void fixTransformedModel(
      @NotNull OpOutputModelProjection<?, ?, ?, ?> old,
      @NotNull OpOutputModelProjection<?, SMP, ?, ?> _new) {

    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames((SMP) old);
  }

  @Override
  protected @NotNull OpOutputVarProjection newEntityRef(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new OpOutputVarProjection(type, location);
  }

  @Override
  protected @NotNull OpOutputModelProjection<?, ?, ?, ?> newModelRef(
      final @NotNull DatumTypeApi model,
      final @NotNull TextLocation location) {
    switch (model.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection((RecordTypeApi) model, location);
      case MAP:
        return new OpOutputMapModelProjection((MapTypeApi) model, location);
      case LIST:
        return new OpOutputListModelProjection((ListTypeApi) model, location);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection((PrimitiveTypeApi) model, location);
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + model.kind());
    }
  }
}
