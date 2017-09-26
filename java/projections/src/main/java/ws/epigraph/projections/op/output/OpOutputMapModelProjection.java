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
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GMapDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpKeyProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.MapTypeApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputMapModelProjection
    extends OpOutputModelProjection<OpOutputModelProjection<?, ?, ?, ?>, OpOutputMapModelProjection, MapTypeApi, GMapDatum>
    implements GenMapModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputMapModelProjection,
    MapTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpOutputKeyProjection keyProjection;
  private /*final @NotNull*/ @Nullable OpOutputVarProjection itemsProjection;

  public OpOutputMapModelProjection(
      @NotNull MapTypeApi model,
      boolean flagged,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull OpOutputKeyProjection keyProjection,
      @NotNull OpOutputVarProjection itemsProjection,
      @Nullable List<OpOutputMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flagged, defaultValue, params, annotations, metaProjection, tails, location);
    this.itemsProjection = itemsProjection;
    this.keyProjection = keyProjection;
  }

  public OpOutputMapModelProjection(final @NotNull MapTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public @NotNull OpOutputKeyProjection keyProjection() {
    assert isResolved();
    assert keyProjection != null;
    return keyProjection;
  }

  @Override
  public @NotNull OpOutputVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  protected OpOutputMapModelProjection clone() {
    if (isResolved) {
      return new OpOutputMapModelProjection(
          model,
          flagged,
          defaultValue,
          params,
          annotations,
          metaProjection,
          keyProjection(),
          itemsProjection(),
          polymorphicTails,
          location()
      );
    } else {
      return new OpOutputMapModelProjection(
          model, location()
      );
    }
  }

  /* static */

  @Override
  protected OpOutputMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final boolean mergedFlagged,
      final @Nullable GMapDatum mergedDefault,
      final @NotNull List<OpOutputMapModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpOutputMapModelProjection> mergedTails) {

    List<OpParams> keysParams = new ArrayList<>(modelProjections.size());
    List<Annotations> keysAnnotations = new ArrayList<>(modelProjections.size());
    OpKeyPresence mergedKeysPresence = null;
    List<OpOutputVarProjection> itemsProjectionsToMerge = new ArrayList<>(modelProjections.size());

    OpOutputMapModelProjection prevProjection = null;
    for (final OpOutputMapModelProjection projection : modelProjections) {

      final /*@NotNull*/ OpOutputKeyProjection keyProjection = projection.keyProjection();
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

    return new OpOutputMapModelProjection(
        model,
        mergedFlagged,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        new OpOutputKeyProjection(
            mergedKeysPresence,
            OpParams.merge(keysParams),
            Annotations.merge(keysAnnotations),
            OpKeyProjection.mergeProjections(
                model.keyType(),
                modelProjections.stream().map(mp -> mp.keyProjection().projection())
            ),
            TextLocation.UNKNOWN
        ),
        itemsProjectionsToMerge.get(0).merge(itemsProjectionsToMerge),
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpOutputMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull OpOutputMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new OpOutputMapModelProjection(
        n.type(),
        n.flagged(),
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
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpOutputMapModelProjection value) {
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
    OpOutputMapModelProjection that = (OpOutputMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
