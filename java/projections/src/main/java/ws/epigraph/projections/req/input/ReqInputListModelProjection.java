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

package ws.epigraph.projections.req.input;

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
public class ReqInputListModelProjection
    extends ReqInputModelProjection<ReqInputModelProjection<?, ?, ?>, ReqInputListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?, ?>,
    ReqInputListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable ReqInputVarProjection itemsProjection;

  public ReqInputListModelProjection(
      @NotNull ListTypeApi model,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull ReqInputVarProjection itemsProjection,
      @Nullable List<ReqInputListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, directives, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public ReqInputListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull ReqInputVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  /* static */
  @Override
  protected ReqInputListModelProjection merge(
      final @NotNull ListTypeApi model,
      final @NotNull List<ReqInputListModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqInputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqInputListModelProjection> mergedTails) {

    List<ReqInputVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqInputListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final /*@NotNull*/ ReqInputVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqInputListModelProjection(
        model,
        mergedParams,
        mergedDirectives,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqInputListModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqInputListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new ReqInputListModelProjection(
        n.type(),
        n.params(),
        n.directives(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqInputListModelProjection value) {
    preResolveCheck(value);
    itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqInputListModelProjection that = (ReqInputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
