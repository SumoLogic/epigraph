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
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.TypeApi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenVarProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?, ?>
    > {

  /**
   * Type this projection applies to.
   * <p/>
   * Projection should be applicable to this type or any of it's subtypes.
   *
   * @return type this projection was constructed for
   */
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

  /**
   * Polymorphic tails for this projection.
   *
   * @return polymorphic tails list.
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic%20tails">polymorphic tails</a>
   */
  @Nullable List<VP> polymorphicTails();

  default @Nullable VP tailByType(@NotNull TypeApi type) {
    Collection<VP> tails = polymorphicTails();
    return tails == null
           ? null
           : tails.stream().filter(t -> t.type().name().equals(type.name())).findFirst().orElse(null);
  }

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

  // references

  /**
   * Gets projection qualified name, if there exists one.
   * <p/>
   * Named projections are used to extract common parts and enable code reuse. For example:
   * <pre><code>
   * namespace com.mycompany
   *
   * outputProjection userProjection: UserRecord (id, firstName, lastName, company $companyProjection)
   * outputProjection companyProjection: CompanyRecord (id, name, logo)
   * </code></pre>
   * {@code userProjection} will get a name of {@code com.mycompany.userProjection} and
   * {@code companyProjection} will get a name of {@code com.mycompany.companyProjection}
   * <p/>
   * Instances will be constructed as follows:
   * <ul>
   * <li>{@code userProjection} reference (empty instance) is created and put in then context</li>
   * <li>projection definition is parsed</li>
   * <li>when {@code companyProjection} name is found: reference is created and put in the context</li>
   * <li>{@code userProjection} construction is complete and reference is {@link #resolve(Qn, GenVarProjection)} resolved}</li>
   * <li>{@code companyProjection} is already in the context, so another reference is not created</li>
   * <li>after {@code companyProjection} construction is complete, this reference is also resolved</li>
   * <li>as the last step it is checked that all projections are {@link #isResolved() resolved}</li>
   * </ul>
   *
   * @return qualified projection name or {@code null} if there is no name.
   */
  @Nullable Qn name();

  /**
   * Resolves this projection reference from another instance. Acts as a copy constructor for all
   * parts except for {@link #type()}, {@link #name()} and {@link #location()}.
   * Is only applicable to reference instances.
   *
   * @param name qualified projection name
   * @param value projection instance to copy state from
   * @see #name()
   */
  void resolve(@NotNull Qn name, @NotNull VP value);

  /**
   * Checks if this projection is resolved, i.e. it's not an empty placeholder instance.
   *
   * @return {@code false} iff this is a reference and is not resolved
   * @see #name()
   */
  boolean isResolved();

  /**
   * Registers a callback to be called after this reference is resolved. Multiple callbacks may be registered.
   *
   * @param callback callback to call when this reference is resolved
   */
  void runOnResolved(@NotNull Runnable callback);

  @NotNull TextLocation location();
}
