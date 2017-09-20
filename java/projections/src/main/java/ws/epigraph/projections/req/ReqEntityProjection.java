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
import ws.epigraph.projections.VarNormalizationContext;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqEntityProjection extends AbstractVarProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>
    > {

  protected /*final*/ boolean flagged;

  public ReqEntityProjection(
      @NotNull TypeApi type,
      boolean flagged,
      @NotNull Map<String, ReqTagProjectionEntry> tagProjections,
      boolean parenthesized,
      @Nullable List<ReqEntityProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, parenthesized, polymorphicTails, location);

    if (tagProjections.size() > 1 && !parenthesized)
      throw new IllegalArgumentException("'parenthesized' must be 'true' for a multi-tag projection");

    ReqTagProjectionEntry singleTagProjection = singleTagProjection();
    this.flagged = flagged || (type.kind() != TypeKind.ENTITY && singleTagProjection != null &&
                               singleTagProjection.projection().flagged());
  }

  public ReqEntityProjection(final @NotNull TypeApi type, final @NotNull TextLocation location) {
    super(type, location);
  }

  public boolean flagged() { return flagged; }

  @Override
  protected ReqEntityProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<ReqEntityProjection> varProjections,
      final @NotNull Map<String, ReqTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<ReqEntityProjection> mergedTails) {

    boolean mergedFlagged = varProjections.stream().anyMatch(ReqEntityProjection::flagged);
    return new ReqEntityProjection(
        effectiveType,
        mergedFlagged,
        mergedTags,
        mergedParenthesized, mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull VarNormalizationContext<ReqEntityProjection> newNormalizationContext() {
    return new VarNormalizationContext<>(
        t -> new ReqEntityProjection(t, location())
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqEntityProjection value) {
    preResolveCheck(value);
    this.flagged = value.flagged();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqEntityProjection that = (ReqEntityProjection) o;
    return flagged == that.flagged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), flagged);
  }
}
