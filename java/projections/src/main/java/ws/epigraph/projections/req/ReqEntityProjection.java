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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.NormalizationContext;
import ws.epigraph.projections.abs.AbstractEntityProjection;
import ws.epigraph.types.TypeApi;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqEntityProjection extends AbstractEntityProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>
    > implements ReqProjection<ReqEntityProjection, ReqModelProjection<?, ?, ?>> {


  public ReqEntityProjection(
      @NotNull TypeApi type,
      boolean flag,
      @NotNull Map<String, ReqTagProjectionEntry> tagProjections,
      boolean parenthesized,
      @Nullable List<ReqEntityProjection> polymorphicTails,
      @NotNull TextLocation location) {

    super(type, flag, tagProjections, parenthesized, polymorphicTails, location);
  }


  public ReqEntityProjection(final @NotNull TypeApi type, final @NotNull TextLocation location) {
    super(type, location);
  }

  public static @NotNull ReqEntityProjection path(
      @NotNull TypeApi type,
      @NotNull ReqTagProjectionEntry tag,
      @NotNull TextLocation location) {

    return new ReqEntityProjection(
        type,
        false,
        Collections.singletonMap(tag.tag().name(), tag),
        false,
        null,
        location
    );
  }

  public static @NotNull ReqEntityProjection pathEnd(@NotNull TypeApi type, @NotNull TextLocation location) {
    return new ReqEntityProjection(type, false, Collections.emptyMap(), false, null, location);
  }


  @Override
  protected ReqEntityProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<ReqEntityProjection> varProjections,
      boolean mergedFlag,
      final @NotNull Map<String, ReqTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<ReqEntityProjection> mergedTails) {

    return new ReqEntityProjection(
        effectiveType,
        mergedFlag,
        mergedTags,
        mergedParenthesized, mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull NormalizationContext<TypeApi, ReqEntityProjection> newNormalizationContext() {
    return new NormalizationContext<>(t -> new ReqEntityProjection(t, location()));
  }

}
