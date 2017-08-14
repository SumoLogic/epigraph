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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.ListTypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteListModelProjection
    extends OpDeleteModelProjection<OpDeleteModelProjection<?, ?, ?>, OpDeleteListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteListModelProjection,
    ListTypeApi
    > {

  private /*final*/ @Nullable OpDeleteVarProjection itemsProjection;

  public OpDeleteListModelProjection(
      @NotNull ListTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteVarProjection itemsProjection,
      @Nullable List<OpDeleteListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public OpDeleteListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.itemsProjection = null;
  }

  @Override
  public @NotNull OpDeleteVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }
  
  /* static */
  @Override
  protected OpDeleteListModelProjection merge(
      final @NotNull ListTypeApi model,
      final @NotNull List<OpDeleteListModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final Annotations mergedAnnotations,
      final @Nullable OpDeleteModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpDeleteListModelProjection> mergedTails) {

    List<OpDeleteVarProjection> itemProjections =
        modelProjections.stream()
            .map(OpDeleteListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final /*@NotNull*/ OpDeleteVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new OpDeleteListModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpDeleteListModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull OpDeleteListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new OpDeleteListModelProjection(
        n.type(),
        n.params(),
        n.annotations(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpDeleteListModelProjection value) {
    preResolveCheck(value);
    this.itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteListModelProjection that = (OpDeleteListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
