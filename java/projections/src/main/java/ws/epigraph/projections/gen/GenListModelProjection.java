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
import ws.epigraph.types.ListTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenListModelProjection<
    P extends GenProjection</*P*/?, TP, ?, ?>,
    TP extends GenTagProjectionEntry<TP, MP>,
    EP extends GenEntityProjection<EP, ?, ?>,
    MP extends GenModelProjection<?, TP, /*MP*/?, /*LMP*/?, /*LMP*/ /*M*/?>,
    LMP extends GenListModelProjection<P, TP, EP, MP, LMP, M>,
    M extends ListTypeApi
    > extends GenModelProjection<EP, TP, MP, LMP, M> {

  @NotNull P itemsProjection();

  @Override
  default boolean isPathEnd() { return false; }
}
