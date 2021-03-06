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
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GListDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.ListTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpListModelProjection
    extends OpModelProjection<OpModelProjection<?, ?, ?, ?>, OpListModelProjection, ListTypeApi, GListDatum>
    implements GenListModelProjection<
    OpProjection<?, ?>,
    OpTagProjectionEntry,
    OpEntityProjection,
    OpModelProjection<?, ?, ?, ?>,
    OpListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpProjection<?, ?> itemsProjection;

  public OpListModelProjection(
      @NotNull ListTypeApi model,
      boolean flag,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable OpProjection<?, ?> itemsProjection,
      @Nullable List<OpListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flag, defaultValue, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public OpListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public static @NotNull OpListModelProjection pathEnd(@NotNull ListTypeApi model, @NotNull TextLocation location) {
    return new OpListModelProjection(
        model,
        false,
        null,
        OpParams.EMPTY,
        Annotations.EMPTY,
        null,
        null, // marks path end
        null,
        location
    );
  }

  @Override
  public boolean isPathEnd() { return itemsProjection == null; }

  @Override
  public @NotNull OpProjection<?, ?> itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  protected OpListModelProjection merge(
      final @NotNull ListTypeApi model,
      final boolean mergedFlag,
      final @Nullable GListDatum mergedDefault,
      final @NotNull List<OpListModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpListModelProjection> mergedTails) {

    List<OpProjection<?, ?>> itemProjections =
        modelProjections.stream()
            .map(OpListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    //noinspection RedundantCast
    final /*@NotNull*/ OpProjection<?, ?> mergedItems = (OpProjection<?, ?>) ProjectionUtils.merge(itemProjections);

    return new OpListModelProjection(
        model,
        mergedFlag,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedItems,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpListModelProjection postNormalizedForType(
      final @NotNull TypeApi targetType,
      final @NotNull OpListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new OpListModelProjection(
        n.type(),
        n.flag(),
        n.defaultValue(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpListModelProjection value) {
    preResolveCheck(value);
    this.itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpListModelProjection that = (OpListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
