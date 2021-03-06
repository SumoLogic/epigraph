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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjection<
    P extends GenProjection</*P*/?, TP, ?, ?>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<?, TP, /*MP*/?, ?, ?>,
    FP extends GenFieldProjection<P, TP, MP, FP>
    > {
//  @NotNull Annotations annotations();

  @NotNull P projection();

  @NotNull FP setProjection(@NotNull P projection);

  /**
   * Merges a list of field projections together
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param type             resulting field type
   * @param fieldProjections field projections to merge, guaranteed to contain at least one element
   *
   * @return merged field projections
   */
  /* static */
  FP merge(@NotNull DataTypeApi type, @NotNull List<FP> fieldProjections);

  @NotNull TextLocation location();
}
