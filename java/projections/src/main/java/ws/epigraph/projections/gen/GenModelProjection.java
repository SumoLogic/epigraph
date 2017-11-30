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
import ws.epigraph.types.TypeApi;

import java.util.List;

/**
 * Generic model projection
 *
 * @param <MP>  model projection type, e.g. {@code OpOutputModelProjection}
 * @param <SMP> specific projection type, e.g. {@code OpOutputRecordModelProjection}
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<
    EP extends GenEntityProjection<EP, /*TP*/?, /*MP*/?>,
    TP extends GenTagProjectionEntry<TP, /*MP*/?>,
    MP extends GenModelProjection</*EP*/?, TP, /*MP*/?, /*SMP*/?, /*TMP*/ ?>,
    SMP extends GenModelProjection</*EP*/?, TP, /*MP*/?, /*SMP*/?, /*TMP*/ /*M*/?>,
    M extends DatumTypeApi
    > extends GenProjection<SMP, TP, EP, MP> {

  @NotNull
  @Override
  M type();

  @Nullable MP metaProjection();

  default @Nullable SMP tailByType(@NotNull DatumTypeApi type) {
    return tailByType((TypeApi) type);
  }

//  @SuppressWarnings("unchecked")
//  @Override
//  default @NotNull SMP merge(@NotNull TypeApi type, @NotNull List<SMP> projections) {
//    assert type instanceof DatumTypeApi;
//    return merge((M) type, projections);
//  }
//
//  /**
//   * Merges a list of models together
//   * <p/>
//   * Should work as a 'static' method: current object should not be merged (most probably it is going
//   * to be the first item of the list anyways). Such design allows for easier implementations that have to
//   * iterate over all the items being merged.
//   *
//   * @param model            resulting model's type
//   * @param modelProjections models to merge
//   *
//   * @return merged models
//   */
//  /* static */
//  @NotNull SMP merge(@NotNull M model, @NotNull List<SMP> modelProjections);

}
