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

package ws.epigraph.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.ListTypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateListModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdateListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable ReqUpdateVarProjection itemsProjection;

  public ReqUpdateListModelProjection(
      @NotNull ListTypeApi model,
      boolean replace,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull ReqUpdateVarProjection itemsProjection,
      @Nullable List<ReqUpdateListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, replace, params, directives, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public ReqUpdateListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull ReqUpdateVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  protected ReqUpdateListModelProjection merge(
      final @NotNull ListTypeApi model,
      final boolean mergedUpdate,
      final @NotNull List<ReqUpdateListModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqUpdateModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqUpdateListModelProjection> mergedTails) {

    List<ReqUpdateVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqUpdateListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull ReqUpdateVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqUpdateListModelProjection(
        model,
        mergedUpdate,
        mergedParams,
        mergedDirectives,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqUpdateListModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqUpdateListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new ReqUpdateListModelProjection(
        n.type(),
        n.replace(),
        n.params(),
        n.directives(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }
  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqUpdateListModelProjection value) {
    preResolveCheck(value);
    itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqUpdateListModelProjection that = (ReqUpdateListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
