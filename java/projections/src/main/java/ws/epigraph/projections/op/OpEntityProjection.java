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

package ws.epigraph.projections.op;

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
public class OpEntityProjection extends AbstractEntityProjection<
    OpProjection<?, ?>,
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>
    > implements OpProjection<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> {

  public OpEntityProjection(
      @NotNull TypeApi type,
      boolean flag,
      @NotNull Map<String, OpTagProjectionEntry> tagProjections,
      boolean parenthesized,
      @Nullable List<OpEntityProjection> polymorphicTails,
      @NotNull TextLocation location) {

    super(type, flag, tagProjections, parenthesized, polymorphicTails, location);
  }

  public static @NotNull OpEntityProjection path(
      @NotNull TypeApi type,
      @NotNull OpTagProjectionEntry tag,
      @NotNull TextLocation location) {

    return new OpEntityProjection(
        type,
        false,
        Collections.singletonMap(tag.tag().name(), tag),
        false,
        null,
        location
    );
  }

  public OpEntityProjection(final TypeApi type, final TextLocation location) {
    super(type, location);
  }

  @Override
  protected OpEntityProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<OpEntityProjection> varProjections,
      boolean mergedFlag,
      final @NotNull Map<String, OpTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<OpEntityProjection> mergedTails) {

    return new OpEntityProjection(
        effectiveType,
        mergedFlag,
        mergedTags,
        mergedParenthesized,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull NormalizationContext<TypeApi, OpProjection<?, ?>> newNormalizationContext() {
    return new OpNormalizationContext();
  }
}
