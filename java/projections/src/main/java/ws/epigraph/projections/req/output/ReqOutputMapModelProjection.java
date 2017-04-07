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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
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
public class ReqOutputMapModelProjection
    extends ReqOutputModelProjection<ReqOutputModelProjection<?, ?, ?>, ReqOutputMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputMapModelProjection,
    MapTypeApi
    > {

  private /*final*/ @Nullable List<ReqOutputKeyProjection> keys;
  private /*final @NotNull*/ @Nullable ReqOutputVarProjection valuesProjection;

  public ReqOutputMapModelProjection(
      @NotNull MapTypeApi model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqOutputKeyProjection> keys,
      @NotNull ReqOutputVarProjection valuesProjection,
      @Nullable List<ReqOutputMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, tails, location);
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  public ReqOutputMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  public @NotNull ReqOutputVarProjection itemsProjection() {
    assert isResolved();
    assert valuesProjection != null;
    return valuesProjection;
  }

  public @Nullable List<ReqOutputKeyProjection> keys() {
    assert isResolved();
    return keys;
  }

  @Override
  protected ReqOutputMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final boolean mergedRequired,
      final @NotNull List<ReqOutputMapModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable ReqOutputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqOutputMapModelProjection> mergedTails,
      final boolean keepPhantomTails) {


    final List<ReqOutputKeyProjection> mergedKeys;

    if (modelProjections.stream().map(ReqOutputMapModelProjection::keys).anyMatch(Objects::isNull)) {
      mergedKeys = null;
    } else {
      //noinspection ConstantConditions
      mergedKeys = ReqKeyProjection.merge(
          modelProjections.stream().flatMap(projection -> projection.keys().stream()),
          (keysToMerge, value, mergedKeyParams, mergedKeyAnnotations) ->
              new ReqOutputKeyProjection(value, mergedKeyParams, mergedKeyAnnotations, TextLocation.UNKNOWN)
      );
    }

    List<ReqOutputVarProjection> itemProjections =
        modelProjections.stream()
            .map(ReqOutputMapModelProjection::itemsProjection)
            .collect(Collectors.toList());

    final @NotNull ReqOutputVarProjection mergedItemsVarType =
        itemProjections.get(0).merge(itemProjections, keepPhantomTails);

    return new ReqOutputMapModelProjection(
        model,
        mergedRequired,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedKeys,
        mergedItemsVarType,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqOutputMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final boolean keepPhantomTails,
      final @NotNull ReqOutputMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new ReqOutputMapModelProjection(
        n.type(),
        n.required(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.keys(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type(), keepPhantomTails),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final ProjectionReferenceName name, final @NotNull ReqOutputMapModelProjection value) {
    super.resolve(name, value);
    keys = value.keys();
    valuesProjection = value.itemsProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputMapModelProjection that = (ReqOutputMapModelProjection) o;
    return Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), keys, valuesProjection); }
}
