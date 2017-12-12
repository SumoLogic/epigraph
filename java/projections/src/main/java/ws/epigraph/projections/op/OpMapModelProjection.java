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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GMapDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.MapTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpMapModelProjection
    extends OpModelProjection<OpModelProjection<?, ?, ?, ?>, OpMapModelProjection, MapTypeApi, GMapDatum>
    implements GenMapModelProjection<
    OpProjection<?, ?>,
    OpTagProjectionEntry,
    OpEntityProjection,
    OpModelProjection<?, ?, ?, ?>,
    OpMapModelProjection,
    MapTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpKeyProjection keyProjection;
  private /*final @NotNull*/ @Nullable OpProjection<?, ?> itemsProjection;

  public OpMapModelProjection(
      @NotNull MapTypeApi model,
      boolean flag,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable OpKeyProjection keyProjection,
      @Nullable OpProjection<?, ?> itemsProjection,
      @Nullable List<OpMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flag, defaultValue, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
    this.keyProjection = keyProjection;
  }

  public OpMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public static @NotNull OpMapModelProjection pathEnd(@NotNull MapTypeApi model, @NotNull TextLocation location) {
    return new OpMapModelProjection(
        model,
        false,
        null,
        OpParams.EMPTY,
        Annotations.EMPTY,
        null,
        null,
        null,
        null,
        location
    );
  }

  @Override
  public boolean isPathEnd() { return itemsProjection == null; }

  public @NotNull OpKeyProjection keyProjection() {
    assert isResolved();
    assert keyProjection != null;
    return keyProjection;
  }

  @Override
  public @NotNull OpProjection<?, ?> itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  /* static */

  @Override
  protected OpMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final boolean mergedFlag,
      final @Nullable GMapDatum mergedDefault,
      final @NotNull List<OpMapModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpMapModelProjection> mergedTails) {

    List<OpParams> keysParams = new ArrayList<>(modelProjections.size());
    List<Annotations> keysAnnotations = new ArrayList<>(modelProjections.size());
    OpKeyPresence mergedKeysPresence = null;
    List<OpProjection<?, ?>> itemsProjectionsToMerge = new ArrayList<>(modelProjections.size());

    OpMapModelProjection prevProjection = null;
    for (final OpMapModelProjection projection : modelProjections) {

      final /*@NotNull*/ OpKeyProjection keyProjection = projection.keyProjection();
      keysParams.add(keyProjection.params());
      keysAnnotations.add(keyProjection.annotations());
      final OpKeyPresence presence = keyProjection.presence();

      if (mergedKeysPresence == null) mergedKeysPresence = presence;
      else {
        /*@Nullable*/
        OpKeyPresence newKeysPresence = OpKeyPresence.merge(mergedKeysPresence, presence);
        if (newKeysPresence == null)
          throw new IllegalArgumentException(
              String.format(
                  "Can't merge key projection defined at %s and key projection defined at %s: incompatible keys presence modes",
                  prevProjection.location(),
                  projection.location()
              )
          );
        mergedKeysPresence = newKeysPresence;
      }

      itemsProjectionsToMerge.add(projection.itemsProjection());

      prevProjection = projection;
    }

    assert mergedKeysPresence != null; // modelProjections should have at least one element
    assert !itemsProjectionsToMerge.isEmpty();

    //noinspection RedundantCast
    return new OpMapModelProjection(
        model,
        mergedFlag,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        new OpKeyProjection(
            mergedKeysPresence,
            TextLocation.UNKNOWN,
            OpParams.merge(keysParams),
            Annotations.merge(keysAnnotations),
            OpKeyProjection.mergeProjections(
                model.keyType(),
                modelProjections.stream().map(mp -> mp.keyProjection().spec())
            ),
            TextLocation.UNKNOWN
        ),
        (OpProjection<?, ?>) ProjectionUtils.merge(itemsProjectionsToMerge),
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpMapModelProjection postNormalizedForType(
      final @NotNull TypeApi targetType,
      final @NotNull OpMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new OpMapModelProjection(
        n.type(),
        n.flag(),
        n.defaultValue(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        n.keyProjection(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpMapModelProjection value) {
    preResolveCheck(value);
    this.keyProjection = value.keyProjection();
    this.itemsProjection = value.itemsProjection();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpMapModelProjection that = (OpMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
