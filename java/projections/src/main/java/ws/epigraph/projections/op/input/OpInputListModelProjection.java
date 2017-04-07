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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GListDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
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
public class OpInputListModelProjection
    extends OpInputModelProjection<OpInputModelProjection<?, ?, ?, ?>, OpInputListModelProjection, ListTypeApi, GListDatum>
    implements GenListModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpInputVarProjection itemsProjection;

  public OpInputListModelProjection(
      @NotNull ListTypeApi model,
      boolean required,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull OpInputVarProjection itemsProjection,
      @Nullable List<OpInputListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, required, defaultValue, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public OpInputListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull OpInputVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  protected OpInputListModelProjection merge(
      final @NotNull ListTypeApi model,
      final boolean mergedRequired,
      final @Nullable GListDatum mergedDefault,
      final @NotNull List<OpInputListModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpInputModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpInputListModelProjection> mergedTails,
      final boolean keepPhantomTails) {
    List<OpInputVarProjection> itemProjections =
        modelProjections.stream()
            .map(OpInputListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull OpInputVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections, keepPhantomTails);

    return new OpInputListModelProjection(
        model,
        mergedRequired, 
        mergedDefault, 
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }
  
  @Override
  public @NotNull OpInputListModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final boolean keepPhantomTails,
      final @NotNull OpInputListModelProjection n) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    return new OpInputListModelProjection(
        n.type(),
        n.required(),
        n.defaultValue(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type(), keepPhantomTails),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }


  @Override
  public void resolve(@NotNull final ProjectionReferenceName name, final @NotNull OpInputListModelProjection value) {
    super.resolve(name, value);
    this.itemsProjection = value.itemsProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputListModelProjection that = (OpInputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
