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

package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.ListTypeApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputListModelProjection
    extends ReqOutputModelProjection<ReqOutputModelProjection<?, ?, ?>, ReqOutputListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputListModelProjection,
    ListTypeApi
    > {

  private /*final @NotNull*/ @Nullable ReqOutputVarProjection itemsProjection;

  public ReqOutputListModelProjection(
      @NotNull ListTypeApi model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputModelProjection<?, ?, ?> metaProjection,
      @NotNull ReqOutputVarProjection itemsProjection,
      @Nullable List<ReqOutputListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public ReqOutputListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  protected @NotNull ModelNormalizationContext<ListTypeApi, ReqOutputListModelProjection> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> new ReqOutputListModelProjection(m, TextLocation.UNKNOWN));
  }

  @Override
  public @NotNull ReqOutputVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  /* static */
  @Override
  protected ReqOutputListModelProjection merge(
      final @NotNull ListTypeApi model,
      final boolean mergedRequired,
      final @NotNull List<ReqOutputListModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable ReqOutputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqOutputListModelProjection> mergedTails,
      final boolean keepPhantomTails) {

    List<ReqOutputVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqOutputListModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull ReqOutputVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections, keepPhantomTails);

    return new ReqOutputListModelProjection(
        model,
        mergedRequired,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public ReqOutputListModelProjection normalizedForType(
      final @NotNull DatumTypeApi targetType,
      final boolean keepPhantomTails) {
    final ListTypeApi targetListType = (ListTypeApi) targetType;
    ReqOutputListModelProjection n = super.normalizedForType(targetType, keepPhantomTails);
    return new ReqOutputListModelProjection(
        n.type(),
        n.required(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.itemsProjection().normalizedForType(targetListType.elementType().type(), keepPhantomTails),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final ProjectionReferenceName name, final @NotNull ReqOutputListModelProjection value) {
    super.resolve(name, value);
    itemsProjection = value.itemsProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputListModelProjection that = (ReqOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
