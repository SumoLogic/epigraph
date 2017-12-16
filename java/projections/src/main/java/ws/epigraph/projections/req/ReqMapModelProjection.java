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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.MapTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqMapModelProjection
    extends ReqModelProjection<ReqModelProjection<?, ?, ?>, ReqMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqProjection<?, ?>,
    ReqTagProjectionEntry,
    ReqEntityProjection,
    ReqModelProjection<?, ?, ?>,
    ReqMapModelProjection,
    MapTypeApi
    > {

  private /*final*/ @Nullable List<ReqKeyProjection> keys;
  private boolean keysRequired;
  private /*final @NotNull*/ @Nullable ReqProjection<?, ?> valuesProjection;

  public ReqMapModelProjection(
      @NotNull MapTypeApi model,
      boolean flag,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqKeyProjection> keys,
      boolean keysRequired,
      @Nullable ReqProjection<?, ?> valuesProjection,
      @Nullable List<ReqMapModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, flag, params, directives, metaProjection, tails, location);
    this.keys = keys == null ? null : Collections.unmodifiableList(keys);
    this.keysRequired = keysRequired;
    this.valuesProjection = valuesProjection;
  }

  public ReqMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public static @NotNull ReqMapModelProjection pathEnd(
      @NotNull MapTypeApi model,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull TextLocation location) {

    return new ReqMapModelProjection(
        model,
        false,
        params,
        directives,
        null,
        null,
        false,
        null,
        null,
        location
    );
  }

  @Override
  public @NotNull ReqProjection<?, ?> itemsProjection() {
    assert isResolved();
    assert valuesProjection != null;
    return valuesProjection;
  }

  public @Nullable List<ReqKeyProjection> keys() {
    assert isResolved();
    return keys;
  }

  @Override
  public boolean isPathEnd() { return valuesProjection == null; }

  public @NotNull ReqKeyProjection pathKey() {
    assert isResolved();
    assert keys != null;
    assert keys.size() == 1;
    return keys.get(0);
  }

  public boolean keysRequired() {
    return keysRequired;
  }

  @Override
  protected ReqMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final boolean mergedFlag,
      final @NotNull List<ReqMapModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqMapModelProjection> mergedTails) {


    final List<ReqKeyProjection> mergedKeys;

    if (modelProjections.stream().map(ReqMapModelProjection::keys).anyMatch(Objects::isNull)) {
      mergedKeys = null;
    } else {
      //noinspection ConstantConditions
      mergedKeys = ReqKeyProjection.merge(
          modelProjections.stream().flatMap(projection -> projection.keys().stream()),
          (keysToMerge, value, mergedKeyParams, mergedKeyAnnotations) ->
              new ReqKeyProjection(value, mergedKeyParams, mergedKeyAnnotations, TextLocation.UNKNOWN)
      );
    }

    List<ReqProjection<?, ?>> itemProjections =
        modelProjections.stream()
            .map(ReqMapModelProjection::itemsProjection)
            .collect(Collectors.toList());

    //noinspection RedundantCast
    final /*@NotNull*/ ReqProjection<?, ?> mergedItemsType =
        (ReqProjection<?, ?>) ProjectionUtils.merge(itemProjections);

    return new ReqMapModelProjection(
        model,
        mergedFlag,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedKeys,
        modelProjections.stream().anyMatch(ReqMapModelProjection::keysRequired),
        mergedItemsType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqMapModelProjection postNormalizedForType(
      final @NotNull TypeApi targetType,
      final @NotNull ReqMapModelProjection n) {

    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new ReqMapModelProjection(
        n.type(),
        n.flag(),
        n.params(),
        n.directives(),
        n.metaProjection(),
        n.keys(),
        n.keysRequired(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqMapModelProjection value) {
    preResolveCheck(value);
    keys = value.keys();
    keysRequired = value.keysRequired();
    valuesProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqMapModelProjection that = (ReqMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           keysRequired == that.keysRequired &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, keysRequired, valuesProjection); }
}
