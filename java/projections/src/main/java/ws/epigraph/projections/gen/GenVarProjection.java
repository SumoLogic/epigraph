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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenVarProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?>
    > {

  @NotNull TypeApi type();

  @NotNull Map<String, TP> tagProjections();

  /**
   * @return single tag if there's just one; {@code null} otherwise
   */
  default @Nullable TP singleTagProjection() {
    final @NotNull Map<String, TP> tagProjections = tagProjections();
    if (tagProjections.size() == 1) return tagProjections.values().iterator().next();
    else return null;
  }

  /**
   * Tells if projection is parenthesized or not.
   * <p/>
   * There are two forms to write down a single tag projection:<br/>
   * <code>:tag tag_projection</code><br/>
   * and<br/>
   * <code>:(tag tag_projection)</code><br/>
   * Semantically they are the same, but sometimes this information can be taken into account.
   * <b>Note</b> that it should not be taken into account by the {@code equals/hashCode} implementation.
   *
   * @return {@code false} iff there's exactly one tag projection and it was not in parenthesis.
   */
  boolean parenthesized();

  @Nullable List<VP> polymorphicTails();

  /**
   * Builds normalized view of this var projection for a given type
   *
   * @param type target type
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  @NotNull VP normalizedForType(@NotNull TypeApi type); // should become `x=tailByType(type); return x==null?this:x;` for fully normalized projections

  /**
   * Merges var projections together.
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param varProjections var projections to merge, guaranteed to contain at least one element
   * @return merged var projection
   */
  /* static */
  @NotNull VP merge(@NotNull List<VP> varProjections);

//  @Nullable
//  default VP tailByType(@NotNull TypeApi tailType) {
//    // not too efficient if there are many tails.. change List to LinkedHashMap?
//    List<VP> tails = polymorphicTails();
//    if (tails == null) return null;
//    return tails.stream().filter(t -> t.type().equals(tailType)).findFirst().orElse(null);
//  }

  @NotNull TextLocation location();
}
