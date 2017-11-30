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
import ws.epigraph.types.TypeKind;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Generic projection
 *
 * @param <P>  this projection type
 * @param <TP> tag projection entry type
 * @param <EP> entity projection type
 * @param <MP> model projection type
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenProjection<
    P extends GenProjection</*P*/?, TP, /*TT*/ /*EP*/?, /*MP*/?>,
    TP extends GenTagProjectionEntry<TP, /*MP*/?>,
    EP extends GenEntityProjection<?, ?, ?>,
    MP extends GenModelProjection<?, ?, ?, ?, ?>
    > extends GenProjectionReference<P> {

  /**
   * Type this projection applies to.
   * <p/>
   * Projection should be applicable to this type or any of it's subtypes.
   *
   * @return type this projection was constructed for
   */
  @Override
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

  boolean flag();

  default boolean isEntityProjection() { return type().kind() == TypeKind.ENTITY; }

  default boolean isModelProjection() { return type().kind() != TypeKind.ENTITY; }

  @SuppressWarnings("unchecked")
  default @NotNull EP asEntityProjection() {
    assert isEntityProjection();
    return (EP) this;
  }

  @SuppressWarnings("unchecked")
  default @NotNull MP asModelProjection() {
    assert isModelProjection();
    return (MP) this;
  }

  /**
   * Polymorphic tails for this projection.
   *
   * @return polymorphic tails list.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails">polymorphic tails</a>
   */
  @Nullable List<P> polymorphicTails();

  /**
   * Builds normalized view of this var projection for a given type
   *
   * @param type target type
   *
   * @return normalized projection without any polymorphic tails. Projection type will be new effective type.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails#normalized-projections">normalized projections</a>
   */
  @NotNull P normalizedForType(@NotNull TypeApi type);

  default @Nullable P tailByType(@NotNull TypeApi type) {
    Collection<P> tails = polymorphicTails();
    return tails == null
           ? null
           : tails.stream().filter(t -> t.type().name().equals(type.name())).findFirst().orElse(null);
  }

  /**
   * Sets normalized tail reference name. Normalized tail produced using
   * {@link #normalizedForType(TypeApi)} will have this reference name assigned
   *
   * @param type              target type
   * @param tailReferenceName normalized tail reference name
   */
  void setNormalizedTailReferenceName(@NotNull TypeApi type, @NotNull ProjectionReferenceName tailReferenceName);

  /**
   * Tells if this projection is a normalized version of some other projection
   *
   * @return another projection which yields this projection if normalized to {@code type()}, or else {@code null}
   */
  @Nullable P normalizedFrom();

  /**
   * Merges projections together.
   * <p/>
   * Should work as a 'static' method: current object should not be merged (most probably it is going
   * to be the first item of the list anyways). Such design allows for easier implementations that have to
   * iterate over all the items being merged.
   *
   * @param type        result type
   * @param projections var projections to merge, guaranteed to contain at least one element
   *
   * @return merged var projection
   */
  /* static */
  @NotNull P merge(@NotNull TypeApi type, @NotNull List<P> projections);

  /**
   * Merges a non-empty list of projections together. Result type is taken from the first
   * element
   */
  /*static*/
  default @NotNull P merge(@NotNull List<P> projections) {
    assert !projections.isEmpty();
    return merge(projections.get(0).type(), projections);
  }

  /**
   * Gets projection reference qualified name, if there exists one.
   * <p/>
   * Named projections are used to extract common parts and enable code reuse. For example:
   * <pre><code>
   * namespace com.mycompany
   *
   * resource myResource : My Type {
   *   outputProjection userProjection: UserRecord (id, firstName, lastName, company $companyProjection)
   *   outputProjection companyProjection: CompanyRecord (id, name, logo)
   * }
   * </code></pre>
   * {@code userProjection} will get a fully qualified name of
   * {@code com.mycompany.resources.myresource.projections.output.userProjection} and
   * {@code companyProjection} will get a fully qualified name of
   * {@code com.mycompany.resources.myresource.projections.output.companyProjection}
   * <p/>
   * Instances will be constructed as follows:
   * <ul>
   * <li>{@code userProjection} reference (empty instance) is created and put in then context</li>
   * <li>projection definition is parsed</li>
   * <li>when {@code companyProjection} name is found: reference is created and put in the context</li>
   * <li>{@code userProjection} construction is complete and reference is {@link GenProjectionReference#resolve(ProjectionReferenceName, GenProjectionReference)} resolved}</li>
   * <li>{@code companyProjection} is already in the context, so another reference is not created</li>
   * <li>after {@code companyProjection} construction is complete, this reference is also resolved</li>
   * <li>as the last step it is checked that all projections are {@link #isResolved() resolved}</li>
   * </ul>
   *
   * @return qualified projection reference name or {@code null} if there is no name.
   */
  @Override
  ProjectionReferenceName referenceName();
}
