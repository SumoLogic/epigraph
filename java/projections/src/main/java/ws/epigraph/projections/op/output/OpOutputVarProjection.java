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

package ws.epigraph.projections.op.output;

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
public class OpOutputVarProjection extends AbstractVarProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>
    > {

  protected /*final*/ boolean flagged;

  public OpOutputVarProjection(
      @NotNull TypeApi type,
      boolean flagged,
      @NotNull Map<String, OpOutputTagProjectionEntry> tagProjections,
      boolean parenthesized,
      @Nullable List<OpOutputVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, tagProjections, parenthesized, polymorphicTails, location);
    //noinspection ConstantConditions
    this.flagged = flagged || (type.kind() != TypeKind.ENTITY && singleTagProjection().projection().flagged());
  }

  public OpOutputVarProjection(final TypeApi type, final TextLocation location) {
    super(type, location);
  }

  public boolean flagged() { return flagged; }

  @Override
  protected OpOutputVarProjection merge(
      final @NotNull TypeApi effectiveType,
      final @NotNull List<OpOutputVarProjection> varProjections,
      final @NotNull Map<String, OpOutputTagProjectionEntry> mergedTags,
      final boolean mergedParenthesized,
      final List<OpOutputVarProjection> mergedTails) {

    boolean mergedFlagged = varProjections.stream().anyMatch(OpOutputVarProjection::flagged);
    return new OpOutputVarProjection(
        effectiveType,
        mergedFlagged,
        mergedTags,
        mergedParenthesized,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull VarNormalizationContext<OpOutputVarProjection> newNormalizationContext() {
    return new VarNormalizationContext<>(
        t -> new OpOutputVarProjection(t, location())
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpOutputVarProjection value) {
    preResolveCheck(value);
    this.flagged = value.flagged();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final OpOutputVarProjection that = (OpOutputVarProjection) o;
    return flagged == that.flagged;
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), flagged); }
}
