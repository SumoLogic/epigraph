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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.MapTypeApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteMapModelProjection
    extends OpDeleteModelProjection<OpDeleteModelProjection<?, ?, ?>, OpDeleteMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteMapModelProjection,
    MapTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpDeleteVarProjection itemsProjection;
  private /*final @NotNull*/ @Nullable OpDeleteKeyProjection keyProjection;

  public OpDeleteMapModelProjection(
      @NotNull MapTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteKeyProjection keyProjection,
      @NotNull OpDeleteVarProjection valuesProjection,
      @Nullable List<OpDeleteMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, tails, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  public OpDeleteMapModelProjection(
      final @NotNull MapTypeApi model,
      final @NotNull TextLocation location) {
    super(model, location);
    itemsProjection = null;
    keyProjection = null;
  }

  @Override
  public @NotNull OpDeleteVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  public @NotNull OpDeleteKeyProjection keyProjection() {
    assert isResolved();
    assert keyProjection != null;
    return keyProjection;
  }
  
  /* static */
  @Override
  protected OpDeleteMapModelProjection merge(
      final @NotNull MapTypeApi model,
      final @NotNull List<OpDeleteMapModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpDeleteModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpDeleteMapModelProjection> mergedTails) {

    // todo unify this code with OpInputMapModelProjection, OpOutputMapModelProjection
    List<OpParams> keysParams = new ArrayList<>(modelProjections.size());
    List<Annotations> keysAnnotations = new ArrayList<>(modelProjections.size());
    OpKeyPresence mergedKeysPresence = null;
    List<OpDeleteVarProjection> itemsProjectionsToMerge = new ArrayList<>(modelProjections.size());

    OpDeleteMapModelProjection prevProjection = null;
    for (final OpDeleteMapModelProjection projection : modelProjections) {

      final @NotNull OpDeleteKeyProjection keyProjection = projection.keyProjection();
      keysParams.add(keyProjection.params());
      keysAnnotations.add(keyProjection.annotations());
      final OpKeyPresence presence = keyProjection.presence();

      if (mergedKeysPresence == null) mergedKeysPresence = presence;
      else {
        @Nullable OpKeyPresence newKeysPresence = OpKeyPresence.merge(mergedKeysPresence, presence);
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

    return new OpDeleteMapModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        new OpDeleteKeyProjection(
            mergedKeysPresence,
            OpParams.merge(keysParams),
            Annotations.merge(keysAnnotations),
            TextLocation.UNKNOWN
        ),
        itemsProjectionsToMerge.get(0).merge(itemsProjectionsToMerge),
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpDeleteMapModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull OpDeleteMapModelProjection n) {
    final MapTypeApi targetMapType = (MapTypeApi) targetType;
    return new OpDeleteMapModelProjection(
        n.type(),
        n.params(),
        n.annotations(),
        n.keyProjection(),
        n.itemsProjection().normalizedForType(targetMapType.valueType().type()),
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpDeleteMapModelProjection value) {
    super.resolve(name, value);
    this.itemsProjection = value.itemsProjection();
    this.keyProjection = value.keyProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteMapModelProjection that = (OpDeleteMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
