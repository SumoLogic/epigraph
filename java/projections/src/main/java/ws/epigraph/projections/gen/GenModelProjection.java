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

package ws.epigraph.projections.gen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.DatumTypeApi;

import java.util.Collection;
import java.util.List;

/**
 * Generic model projection
 *
 * @param <MP>  model projection type, e.g. {@code OpOutputModelProjection}
 * @param <SMP> specific projection type, e.g. {@code OpOutputRecordModelProjection}
 * @param <TMP> tail model projection type, almost always same as {@code SMP}. Possible exception in the future: any/abstract type
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<
    TP extends GenTagProjectionEntry<TP, /*MP*/?>,
    MP extends GenModelProjection<TP, /*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    SMP extends GenModelProjection<TP, /*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    TMP extends GenModelProjection<TP, /*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    M extends DatumTypeApi
    > extends GenProjection<SMP, TP> {

  @Override
  @NotNull M type();

  @Nullable MP metaProjection();

  @Nullable List<TMP> polymorphicTails();

  default @Nullable TMP tailByType(@NotNull DatumTypeApi type) {
    Collection<TMP> tails = polymorphicTails();
    return tails == null
           ? null
           : tails.stream().filter(t -> t.type().name().equals(type.name())).findFirst().orElse(null);
  }

  /**
   * Builds normalized view of this model projection for a given type
   *
   * @param type target type
   *
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  default @NotNull TMP normalizedForType(@NotNull DatumTypeApi type) { return normalizedForType(type, null); }

  /**
   * Builds normalized view of this model projection for a given type
   *
   * @param type                target type
   * @param resultReferenceName optional result reference name
   *
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  @Deprecated
  @NotNull TMP normalizedForType(@NotNull DatumTypeApi type, @Nullable ProjectionReferenceName resultReferenceName);

  /**
   * Sets normalized tail reference name. Normalized tail produced using
   * {@link #normalizedForType(DatumTypeApi)} will have this reference name assigned
   *
   * @param type              target type
   * @param tailReferenceName normalized tail reference name
   */
  void setNormalizedTailReferenceName(@NotNull DatumTypeApi type, @NotNull ProjectionReferenceName tailReferenceName);

  /**
   * Merges a list of models together
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param model            resulting model's type
   * @param modelProjections models to merge
   *
   * @return merged models or {@code null} if {@code modelProjections} is empty
   */
  /* static */
  @Nullable SMP merge(@NotNull M model, @NotNull List<SMP> modelProjections);

//  @SuppressWarnings("unchecked")
//  default @NotNull SMP mergeWith(@NotNull SMP other) {
//    List<SMP> toMerge = new ArrayList<>(2);
//    toMerge.add((SMP) this);
//    toMerge.add(other);
//    return merge(type(), toMerge, );
//  }

  /**
   * Tells if this projection is a normalized version of some other projection
   *
   * @return another projection which yields this projection if normalized to {@code type()} or else {@code null}
   */
  @Nullable SMP normalizedFrom();
}
