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

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenProjection<
    P extends GenProjection</*P*/?, /*TP*/?>,
    TP extends GenTagProjectionEntry</*TP*/?, /*MP*/?>
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

  // todo should take type as a parameter
//  /**
//   * Merges var projections together.
//   * <p/>
//   * Should work as a 'static' method: current object should not be merged (most probably it is going
//   * to be the first item of the list anyways). Such design allows for easier implementations that have to
//   * iterate over all the items being merged.
//   *
//   * @param projections var projections to merge, guaranteed to contain at least one element
//   *
//   * @return merged var projection
//   */
//  /* static */
//  @NotNull P merge(@NotNull List<P> projections);

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
