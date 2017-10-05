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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.ListTypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqListModelProjection
    extends ReqModelProjection<ReqModelProjection<?, ?, ?>, ReqListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable ReqEntityProjection itemsProjection;

  public ReqListModelProjection(
      @NotNull ListTypeApi model,
      boolean flag,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @NotNull ReqEntityProjection itemsProjection,
      @Nullable List<ReqListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flag, params, directives, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public ReqListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull ReqEntityProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  protected ReqListModelProjection clone() {
    if (isResolved) {
      return new ReqListModelProjection(
          model,
          flag,
          params,
          directives,
          metaProjection,
          itemsProjection(),
          polymorphicTails,
          location()
      );
    } else {
      return new ReqListModelProjection(model, location());
    }
  }

  /* static */
  @Override
  protected ReqListModelProjection merge(
      final @NotNull ListTypeApi model,
      final boolean mergedFlag,
      final @NotNull List<ReqListModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqListModelProjection> mergedTails) {

    List<ReqEntityProjection> itemProjections =
        modelProjections.stream()
            .map(ReqListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final /*@NotNull*/ ReqEntityProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqListModelProjection(
        model,
        mergedFlag,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqListModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new ReqListModelProjection(
        n.type(),
        n.flag(),
        n.params(),
        n.directives(),
        n.metaProjection(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqListModelProjection value) {
    preResolveCheck(value);
    itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqListModelProjection that = (ReqListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
