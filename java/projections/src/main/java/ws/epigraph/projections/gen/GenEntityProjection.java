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
import ws.epigraph.types.TypeApi;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenEntityProjection<
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, /*MP*/?>,
    MP extends GenModelProjection</*TP*/?, /*MP*/?, ?, ?, ?>
    > extends GenProjection<EP, TP> {

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

  /**
   * Polymorphic tails for this projection.
   *
   * @return polymorphic tails list.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails">polymorphic tails</a>
   */
  @Nullable List<EP> polymorphicTails();

  default @Nullable EP tailByType(@NotNull TypeApi type) {
    Collection<EP> tails = polymorphicTails();
    return tails == null
           ? null
           : tails.stream().filter(t -> t.type().name().equals(type.name())).findFirst().orElse(null);
  }

  /**
   * Builds normalized view of this var projection for a given type
   *
   * @param type target type
   *
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  default @NotNull EP normalizedForType(@NotNull TypeApi type) { return normalizedForType(type, null); }

  /**
   * Builds normalized view of this var projection for a given type
   *
   * @param type                target type
   * @param resultReferenceName optional result reference name
   *
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  @Deprecated
  @NotNull EP normalizedForType(@NotNull TypeApi type, @Nullable ProjectionReferenceName resultReferenceName);

  /**
   * Sets normalized tail reference name. Normalized tail produced using
   * {@link #normalizedForType(TypeApi)} will have this reference name assigned
   *
   * @param type              target type
   * @param tailReferenceName normalized tail reference name
   */
  void setNormalizedTailReferenceName(@NotNull TypeApi type, @NotNull ProjectionReferenceName tailReferenceName);

//  @Nullable
//  default VP tailByType(@NotNull TypeApi tailType) {
//    // not too efficient if there are many tails.. change List to LinkedHashMap?
//    List<VP> tails = polymorphicTails();
//    if (tails == null) return null;
//    return tails.stream().filter(t -> t.type().equals(tailType)).findFirst().orElse(null);
//  }
  /**
   * Merges var projections together.
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param projections var projections to merge, guaranteed to contain at least one element
   *
   * @return merged var projection
   */
  /* static */
  @NotNull EP merge(@NotNull List<EP> projections);

  // references

  /**
   * Tells if this projection is a normalized version of some other projection
   *
   * @return another projection which yields this projection if normalized to {@code type()} or else {@code null}
   */
  @Nullable EP normalizedFrom();
}
