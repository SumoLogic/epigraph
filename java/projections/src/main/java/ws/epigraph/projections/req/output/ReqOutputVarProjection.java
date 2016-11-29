/*
 * Copyright 2016 Sumo Logic
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
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.Type;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputVarProjection extends AbstractVarProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>
    > {
  private final boolean parenthesized;
  // if parens were present, e.g. `:(id)` vs `:id`. Tells marshaller if to use multi- or single-var

  public ReqOutputVarProjection(
      @NotNull Type type,
      @NotNull Map<String, ReqOutputTagProjectionEntry> tagProjections,
      @Nullable List<ReqOutputVarProjection> polymorphicTails,
      boolean parenthesized,
      @NotNull TextLocation location) {
    super(type, tagProjections, polymorphicTails, location);
    this.parenthesized = parenthesized;
  }

  public boolean parenthesized() { return parenthesized; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputVarProjection that = (ReqOutputVarProjection) o;
    return parenthesized == that.parenthesized;
  }

  @Override
  protected ReqOutputVarProjection merge(
      @NotNull final Type type,
      @NotNull final List<ReqOutputVarProjection> varProjections,
      @NotNull final Map<String, ReqOutputTagProjectionEntry> mergedTags,
      final List<ReqOutputVarProjection> mergedTails) {

    return new ReqOutputVarProjection(
        type,
        mergedTags,
        mergedTails,
        varProjections.stream().anyMatch(ReqOutputVarProjection::parenthesized),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), parenthesized);
  }
}
