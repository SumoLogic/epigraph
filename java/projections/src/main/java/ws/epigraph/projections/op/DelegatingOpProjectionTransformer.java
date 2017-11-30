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
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DelegatingOpProjectionTransformer extends OpProjectionTransformer {
//  private final OpProjectionTransformer delegate;
//
//  public DelegatingOpProjectionTransformer(final OpProjectionTransformer delegate) {this.delegate = delegate;}
//
//  @Override
//  public OpEntityProjection transformEntityProjection(
//      final @NotNull OpEntityProjection entityProjection,
//      final @Nullable DataTypeApi dataType,
//      final @NotNull Map<String, OpTagProjectionEntry> transformedTagProjections,
//      final @Nullable List<OpEntityProjection> transformedTails,
//      final boolean mustRebuild) {
//    return delegate.transformEntityProjection(
//        entityProjection,
//        dataType,
//        transformedTagProjections,
//        transformedTails,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpTagProjectionEntry transformTagProjection(
//      final @NotNull OpEntityProjection entityProjection,
//      final @NotNull TagApi tag,
//      final @NotNull OpTagProjectionEntry tagProjection,
//      final @NotNull OpModelProjection<?, ?, ?, ?> transformedModelProjection,
//      final boolean mustRebuild) {
//    return delegate.transformTagProjection(
//        entityProjection,
//        tag,
//        tagProjection,
//        transformedModelProjection,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpRecordModelProjection transformRecordProjection(
//      final @NotNull OpRecordModelProjection recordModelProjection,
//      final @NotNull Map<String, OpFieldProjectionEntry> transformedFields,
//      final @Nullable List<OpRecordModelProjection> transformedTails,
//      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta, final boolean mustRebuild) {
//    return delegate.transformRecordProjection(
//        recordModelProjection,
//        transformedFields,
//        transformedTails,
//        transformedMeta,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpFieldProjection transformFieldProjection(
//      final @NotNull OpFieldProjection fieldProjection,
//      final @NotNull OpEntityProjection transformedEntityProjection,
//      final boolean mustRebuild) {
//    return delegate.transformFieldProjection(
//        fieldProjection,
//        transformedEntityProjection,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpFieldProjectionEntry transformFieldProjectionEntry(
//      final @NotNull OpRecordModelProjection modelProjection,
//      final @NotNull OpFieldProjectionEntry fieldProjectionEntry,
//      final @NotNull OpFieldProjection transformedFieldProjection, final boolean mustRebuild) {
//    return delegate.transformFieldProjectionEntry(
//        modelProjection,
//        fieldProjectionEntry,
//        transformedFieldProjection,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpMapModelProjection transformMapProjection(
//      final @NotNull OpMapModelProjection mapModelProjection,
//      final @NotNull OpEntityProjection transformedItemsProjection,
//      final @Nullable List<OpMapModelProjection> transformedTails,
//      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta, final boolean mustRebuild) {
//    return delegate.transformMapProjection(
//        mapModelProjection,
//        transformedItemsProjection,
//        transformedTails,
//        transformedMeta,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpListModelProjection transformListProjection(
//      final @NotNull OpListModelProjection listModelProjection,
//      final @NotNull OpEntityProjection transformedItemsProjection,
//      final @Nullable List<OpListModelProjection> transformedTails,
//      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta, final boolean mustRebuild) {
//    return delegate.transformListProjection(
//        listModelProjection,
//        transformedItemsProjection,
//        transformedTails,
//        transformedMeta,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public @NotNull OpPrimitiveModelProjection transformPrimitiveProjection(
//      final @NotNull OpPrimitiveModelProjection primitiveModelProjection,
//      final @Nullable List<OpPrimitiveModelProjection> transformedTails,
//      final @Nullable OpModelProjection<?, ?, ?, ?> transformedMeta,
//      final boolean mustRebuild) {
//    return delegate.transformPrimitiveProjection(
//        primitiveModelProjection,
//        transformedTails,
//        transformedMeta,
//        mustRebuild
//    );
//  }
//
//  @Override
//  public <SMP extends OpModelProjection<?, SMP, ?, ?>> void fixTransformedModel(
//      final @NotNull OpModelProjection<?, ?, ?, ?> old,
//      final @NotNull OpModelProjection<?, SMP, ?, ?> _new) {delegate.fixTransformedModel(old, _new);}
//
//  @Override
//  public @NotNull OpEntityProjection newEntityRef(
//      final @NotNull TypeApi type,
//      final @NotNull TextLocation location) {return delegate.newEntityRef(type, location);}
//
//  @Override
//  public @NotNull OpModelProjection<?, ?, ?, ?> newModelRef(
//      final @NotNull DatumTypeApi model,
//      final @NotNull TextLocation location) {return delegate.newModelRef(model, location);}
//
//  @Override
//  public @NotNull OpProjectionTransformationMapImpl newTransformationMap() {return delegate.newTransformationMap();}
}
