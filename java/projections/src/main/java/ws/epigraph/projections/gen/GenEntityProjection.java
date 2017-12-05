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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenEntityProjection<
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, /*MP*/?>,
    MP extends GenModelProjection<EP, /*TP*/?, /*MP*/?, ?, ?>
    > extends GenProjection<EP, TP, EP, MP> {

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

  @Override
  default boolean isPathEnd() { return tagProjections().isEmpty(); }
}
