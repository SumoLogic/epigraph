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
import ws.epigraph.projections.req.output.*;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqProjectionTransformer extends GenProjectionTransformer<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputMapModelProjection,
    ReqOutputListModelProjection,
    ReqOutputPrimitiveModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection
    > {

  // NB keep in sync with OpProjectionTransformer

  @Override
  protected ReqOutputVarProjection transformVarProjection(
      final @NotNull ReqOutputVarProjection varProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, ReqOutputTagProjectionEntry> transformedTagProjections,
      final @Nullable List<ReqOutputVarProjection> transformedTails,
      final boolean mustRebuild) {

    if (mustRebuild) {
      ReqOutputVarProjection newProjection = new ReqOutputVarProjection(
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
  protected @NotNull ReqOutputTagProjectionEntry transformTagProjection(
      final @NotNull ReqOutputVarProjection varProjection,
      final @NotNull TagApi tag,
      final @NotNull ReqOutputTagProjectionEntry tagProjection,
      final @NotNull ReqOutputModelProjection<?, ?, ?> transformedModelProjection,
      final boolean mustRebuild) {

    return mustRebuild ?
           new ReqOutputTagProjectionEntry(
               tag,
               transformedModelProjection,
               tagProjection.location()
           ) : tagProjection;
  }

  @Override
  protected @NotNull ReqOutputRecordModelProjection transformRecordModelProjection(
      final @NotNull ReqOutputRecordModelProjection recordModelProjection,
      final @NotNull Map<String, ReqOutputFieldProjectionEntry> transformedFields,
      final @Nullable List<ReqOutputRecordModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      ReqOutputRecordModelProjection newProjection = new ReqOutputRecordModelProjection(
          recordModelProjection.type(),
          recordModelProjection.flagged(),
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
  protected @NotNull ReqOutputFieldProjection transformFieldProjection(
      final @NotNull ReqOutputFieldProjection fieldProjection,
      final @NotNull ReqOutputVarProjection transformedEntityProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new ReqOutputFieldProjection(
        transformedEntityProjection,
        fieldProjection.location()
    ) : fieldProjection;
  }

  @Override
  protected @NotNull ReqOutputFieldProjectionEntry transformFieldProjectionEntry(
      final @NotNull ReqOutputRecordModelProjection modelProjection,
      final @NotNull ReqOutputFieldProjectionEntry fieldProjectionEntry,
      final @NotNull ReqOutputFieldProjection transformedFieldProjection,
      final boolean mustRebuild) {

    return mustRebuild ? new ReqOutputFieldProjectionEntry(
        fieldProjectionEntry.field(),
        transformedFieldProjection,
        fieldProjectionEntry.location()
    ) : fieldProjectionEntry;
  }

  @Override
  protected @NotNull ReqOutputMapModelProjection transformMapModelProjection(
      final @NotNull ReqOutputMapModelProjection mapModelProjection,
      final @NotNull ReqOutputVarProjection transformedItemsProjection,
      final @Nullable List<ReqOutputMapModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      ReqOutputMapModelProjection newProjection = new ReqOutputMapModelProjection(
          mapModelProjection.type(),
          mapModelProjection.flagged(),
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
  protected @NotNull ReqOutputListModelProjection transformListModelProjection(
      final @NotNull ReqOutputListModelProjection listModelProjection,
      final @NotNull ReqOutputVarProjection transformedItemsProjection,
      final @Nullable List<ReqOutputListModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      ReqOutputListModelProjection newProjection = new ReqOutputListModelProjection(
          listModelProjection.type(),
          listModelProjection.flagged(),
          listModelProjection.params(),
          listModelProjection.directives(),
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
  protected @NotNull ReqOutputPrimitiveModelProjection transformPrimitiveModelProjection(
      final @NotNull ReqOutputPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqOutputPrimitiveModelProjection> transformedTails,
      final @Nullable ReqOutputModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (mustRebuild) {
      ReqOutputPrimitiveModelProjection newProjection = new ReqOutputPrimitiveModelProjection(
          primitiveModelProjection.type(),
          primitiveModelProjection.flagged(),
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

  protected void fixTransformedEntity(@NotNull ReqOutputVarProjection old, @NotNull ReqOutputVarProjection _new) {
    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames(old);
  }

  @SuppressWarnings("unchecked")
  protected <SMP extends ReqOutputModelProjection<?, SMP, ?>> void fixTransformedModel(
      @NotNull ReqOutputModelProjection<?, ?, ?> old,
      @NotNull ReqOutputModelProjection<?, SMP, ?> _new) {

    _new.setReferenceName(old.referenceName());
    _new.copyNormalizedTailReferenceNames((SMP) old);
  }

  @Override
  protected @NotNull ReqOutputVarProjection newEntityRef(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new ReqOutputVarProjection(type, location);
  }

  @Override
  protected @NotNull ReqOutputModelProjection<?, ?, ?> newModelRef(
      final @NotNull DatumTypeApi model,
      final @NotNull TextLocation location) {
    switch (model.kind()) {
      case RECORD:
        return new ReqOutputRecordModelProjection((RecordTypeApi) model, location);
      case MAP:
        return new ReqOutputMapModelProjection((MapTypeApi) model, location);
      case LIST:
        return new ReqOutputListModelProjection((ListTypeApi) model, location);
      case PRIMITIVE:
        return new ReqOutputPrimitiveModelProjection((PrimitiveTypeApi) model, location);
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + model.kind());
    }
  }
}
