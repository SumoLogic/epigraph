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

package ws.epigraph.projections.req.delete;

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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteMapModelProjection
    extends ReqDeleteModelProjection<ReqDeleteModelProjection<?, ?, ?>, ReqDeleteMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?, ?>,
    ReqDeleteMapModelProjection,
    MapTypeApi
    > {

  private /*final*/ @Nullable List<ReqDeleteKeyProjection> keys;
  private /*final @NotNull*/ @Nullable ReqDeleteVarProjection valuesProjection;

  public ReqDeleteMapModelProjection(
      @NotNull MapTypeApi model,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqDeleteKeyProjection> keys,
      @NotNull ReqDeleteVarProjection valuesProjection,
      @Nullable List<ReqDeleteMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, directives, tails, location);
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  public ReqDeleteMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }


  @Override
  public @NotNull ReqDeleteVarProjection itemsProjection() {
    assert isResolved();
    assert valuesProjection != null;
    return valuesProjection;
  }

  public @Nullable List<ReqDeleteKeyProjection> keys() {
    assert isResolved();
    return keys;
  }

  @Override
  protected ReqDeleteMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final @NotNull List<ReqDeleteMapModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqDeleteModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqDeleteMapModelProjection> mergedTails) {

    final List<ReqDeleteKeyProjection> mergedKeys;

    if (modelProjections.stream().map(ReqDeleteMapModelProjection::keys).anyMatch(Objects::isNull)) {
      mergedKeys = null;
    } else {
      //noinspection ConstantConditions
      mergedKeys = ReqKeyProjection.merge(
          modelProjections.stream().flatMap(projection -> projection.keys().stream()),
          (keysToMerge, value, mergedKeyParams, mergedKeyAnnotations) ->
              new ReqDeleteKeyProjection(value, mergedKeyParams, mergedKeyAnnotations, TextLocation.UNKNOWN)
      );
    }

    List<ReqDeleteVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqDeleteMapModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull ReqDeleteVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqDeleteMapModelProjection(
        model,
        mergedParams,
        mergedDirectives,
        mergedKeys,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqDeleteMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqDeleteMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new ReqDeleteMapModelProjection(
        n.type(),
        n.params(),
        n.directives(),
        n.keys(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqDeleteMapModelProjection value) {
    preResolveCheck(value);
    keys = value.keys();
    valuesProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqDeleteMapModelProjection that = (ReqDeleteMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
