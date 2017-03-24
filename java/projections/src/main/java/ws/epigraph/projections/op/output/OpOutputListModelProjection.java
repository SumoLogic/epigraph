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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.ListTypeApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputListModelProjection
    extends OpOutputModelProjection<OpOutputModelProjection<?, ?, ?>, OpOutputListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?>,
    OpOutputListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpOutputVarProjection itemsProjection;

  public OpOutputListModelProjection(
      @NotNull ListTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @NotNull OpOutputVarProjection itemsProjection,
      @Nullable List<OpOutputListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
  }

  protected OpOutputListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull OpOutputVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  /* static */
  @Override
  protected OpOutputListModelProjection merge(
      final @NotNull ListTypeApi model,
      final @NotNull List<OpOutputListModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpOutputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpOutputListModelProjection> mergedTails) {

    List<OpOutputVarProjection> itemProjections =
        modelProjections.stream()
            .map(OpOutputListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull OpOutputVarProjection mergedItemsVarType = itemProjections.get(0).merge(itemProjections);

    return new OpOutputListModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpOutputListModelProjection normalizedForType(final @NotNull DatumTypeApi targetType) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    OpOutputListModelProjection n = super.normalizedForType(targetType);
    return new OpOutputListModelProjection(
        n.type(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @NotNull Qn name, final @NotNull OpOutputListModelProjection value) {
    super.resolve(name, value);
    this.itemsProjection = value.itemsProjection();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputListModelProjection that = (OpOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
