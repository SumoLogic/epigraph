/*
 * Copyright 2016 Sumo Logic
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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
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
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    SMP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    TMP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, ?>,
    M extends DatumTypeApi> {

  @NotNull M model();

  @Nullable MP metaProjection();

  @Nullable List<TMP> polymorphicTails();

  default @Nullable TMP tailByType(@NotNull DatumTypeApi type) {
    Collection<TMP> tails = polymorphicTails();
    return tails == null
           ? null
           : tails.stream().filter(t -> t.model().name().equals(type.name())).findFirst().orElse(null);
  }

  /**
   * Builds normalized view of this model projection for a given type
   *
   * @param type target type
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  @NotNull TMP normalizedForType(@NotNull DatumTypeApi type); // should become `x=tailByType(type); return x==null?this:x;` for fully normalized projections

  /**
   * Merges a list of models together
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param model            resulting model's type
   * @param modelProjections models to merge
   * @return merged models or {@code null} if {@code modelProjections} is empty
   */
  /* static */
  SMP merge(@NotNull M model, @NotNull List<SMP> modelProjections);

  @NotNull Annotations annotations();

  @NotNull TextLocation location();
}
