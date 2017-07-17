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

package ws.epigraph.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.VarNormalizationContext;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.TypeApi;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateVarProjection extends AbstractVarProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>
    > {

  private boolean replace;

  public ReqUpdateVarProjection(
      @NotNull TypeApi type,
      boolean replace,
      @NotNull Map<String, ReqUpdateTagProjectionEntry> tagProjections,
      boolean parenthesized,
      @Nullable List<ReqUpdateVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, parenthesized, polymorphicTails, location);
    this.replace = replace;
  }

  public ReqUpdateVarProjection(final @NotNull TypeApi type, final @NotNull TextLocation location) {
    super(type, location);
    replace = false;
  }

  /**
   * @return {@code true} if this entity must be replaced (updated), {@code false} if it must be patched
   */
  public boolean replace() { return replace; }

  @Override
  protected ReqUpdateVarProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<ReqUpdateVarProjection> varProjections,
      final @NotNull Map<String, ReqUpdateTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<ReqUpdateVarProjection> mergedTails) {

    return new ReqUpdateVarProjection(
        effectiveType,
        varProjections.stream().anyMatch(vp -> vp.replace),
        mergedTags,
        mergedParenthesized, mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqUpdateVarProjection value) {
    preResolveCheck(value);
    this.replace = value.replace;
    super.resolve(name, value);
  }

  @Override
  protected @NotNull VarNormalizationContext<ReqUpdateVarProjection> newNormalizationContext() {
    return new VarNormalizationContext<>(t -> new ReqUpdateVarProjection(t, location()));
  }

}
