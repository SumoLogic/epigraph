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
public class ReqInputMapModelProjection
    extends ReqInputModelProjection<ReqInputModelProjection<?, ?, ?>, ReqInputMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?, ?>,
    ReqInputMapModelProjection,
    MapTypeApi
    > {

  private /*final*/ @Nullable List<ReqInputKeyProjection> keys;
  private /*final @NotNull*/ @Nullable ReqInputVarProjection valuesProjection;

  public ReqInputMapModelProjection(
      @NotNull MapTypeApi model,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable List<ReqInputKeyProjection> keys,
      @NotNull ReqInputVarProjection valuesProjection,
      @Nullable List<ReqInputMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, directives, tails, location);
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  public ReqInputMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull ReqInputVarProjection itemsProjection() {
    assert isResolved();
    assert valuesProjection != null;
    return valuesProjection;
  }

  public @Nullable List<ReqInputKeyProjection> keys() {
    assert isResolved();
    return keys;
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqInputMapModelProjection value) {
    preResolveCheck(value);
    this.keys = value.keys();
    this.valuesProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  protected ReqInputMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final @NotNull List<ReqInputMapModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqInputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqInputMapModelProjection> mergedTails) {


    final List<ReqInputKeyProjection> mergedKeys;

    if (modelProjections.stream().map(ReqInputMapModelProjection::keys).anyMatch(Objects::isNull)) {
      mergedKeys = null;
    } else {
      //noinspection ConstantConditions
      mergedKeys = ReqKeyProjection.merge(
          modelProjections.stream().flatMap(projection -> projection.keys().stream()),
          (keysToMerge, value, mergedKeyParams, mergedKeyAnnotations) ->
              new ReqInputKeyProjection(value, mergedKeyParams, mergedKeyAnnotations, TextLocation.UNKNOWN)
      );
    }

    List<ReqInputVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqInputMapModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final /*@NotNull*/ ReqInputVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections);

    return new ReqInputMapModelProjection(
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
  protected @NotNull ReqInputMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqInputMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new ReqInputMapModelProjection(
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqInputMapModelProjection that = (ReqInputMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
