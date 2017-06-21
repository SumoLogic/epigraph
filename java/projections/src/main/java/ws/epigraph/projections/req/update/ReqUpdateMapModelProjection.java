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
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.ReqKeyProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.MapTypeApi;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateMapModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdateMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateMapModelProjection,
    MapTypeApi
    > {

  private /*final*/ @NotNull List<ReqUpdateKeyProjection> keys;
  private /*final @NotNull*/ @Nullable ReqUpdateVarProjection valuesProjection;

  public ReqUpdateMapModelProjection(
      @NotNull MapTypeApi model,
      boolean replace,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull List<ReqUpdateKeyProjection> keys,
      @NotNull ReqUpdateVarProjection valuesProjection,
      @Nullable List<ReqUpdateMapModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, replace, params, directives, tails, location);
    this.keys = Collections.unmodifiableList(keys);
    this.valuesProjection = valuesProjection;
  }

  public ReqUpdateMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    keys = Collections.emptyList();
  }

  @Override
  public @NotNull ReqUpdateVarProjection itemsProjection() {
    assert isResolved();
    assert valuesProjection != null;
    return valuesProjection;
  }

  public @NotNull List<ReqUpdateKeyProjection> keys() {
    assert isResolved();
    return keys;
  }

  @Override
  protected ReqUpdateMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final boolean mergedUpdate,
      final @NotNull List<ReqUpdateMapModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqUpdateModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqUpdateMapModelProjection> mergedTails) {

    final List<ReqUpdateKeyProjection> mergedKeys;

    //noinspection ConstantConditions
    mergedKeys = ReqKeyProjection.merge(
        modelProjections.stream().flatMap(projection -> projection.keys().stream()),
        (keysToMerge, value, mergedKeyParams, mergedKeyAnnotations) ->
            new ReqUpdateKeyProjection(value, mergedKeyParams, mergedKeyAnnotations, TextLocation.UNKNOWN)
    );

    List<ReqUpdateVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqUpdateMapModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull ReqUpdateVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqUpdateMapModelProjection(
        model,
        mergedUpdate,
        mergedParams,
        mergedDirectives,
        mergedKeys,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqUpdateMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqUpdateMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new ReqUpdateMapModelProjection(
        n.type(),
        n.replace(),
        n.params(),
        n.directives(),
        n.keys(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqUpdateMapModelProjection value) {
    super.resolve(name, value);
    keys = value.keys();
    valuesProjection = value.itemsProjection();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqUpdateMapModelProjection that = (ReqUpdateMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
