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

package ws.epigraph.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.VarNormalizationContext;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputVarProjection extends AbstractVarProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>
    > {

  public ReqOutputVarProjection(
      @NotNull TypeApi type,
      @NotNull Map<String, ReqOutputTagProjectionEntry> tagProjections,
      @Nullable List<ReqOutputVarProjection> polymorphicTails,
      boolean parenthesized,
      @NotNull TextLocation location) {
    super(type, tagProjections, parenthesized, polymorphicTails, location);

    if (tagProjections.size() > 1 && !parenthesized)
      throw new IllegalArgumentException("'parenthesized' must be 'true' for a multi-tag projection");
  }

  public ReqOutputVarProjection(final @NotNull TypeApi type, final @NotNull TextLocation location) {
    super(type, location);
  }

  @Override
  protected ReqOutputVarProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<ReqOutputVarProjection> varProjections,
      final @NotNull Map<String, ReqOutputTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<ReqOutputVarProjection> mergedTails) {

    return new ReqOutputVarProjection(
        effectiveType,
        mergedTags,
        mergedTails,
        mergedParenthesized,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull VarNormalizationContext<ReqOutputVarProjection> newNormalizationContext() {
    return new VarNormalizationContext<>(
        t -> new ReqOutputVarProjection(t, location())
    );
  }

}
