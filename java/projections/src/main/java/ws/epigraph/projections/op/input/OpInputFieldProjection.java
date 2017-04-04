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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.AbstractOpFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputFieldProjection extends AbstractOpFieldProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputFieldProjection
    > {

  private final boolean required;

  public OpInputFieldProjection(
//      @NotNull OpParams params,
//      @NotNull Annotations annotations,
      @NotNull OpInputVarProjection projection,
      boolean required,
      @NotNull TextLocation location) {
    super(/*params, annotations, */projection, location);
    this.required = required;
  }

  public boolean required() { return required; }

  @Override
  public @NotNull OpInputFieldProjection setVarProjection(final @NotNull OpInputVarProjection varProjection) {
    return new OpInputFieldProjection(varProjection, required, TextLocation.UNKNOWN);
  }

  @Override
  protected OpInputFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<OpInputFieldProjection> fieldProjections,
      final @NotNull OpInputVarProjection mergedVarProjection) {

    return new OpInputFieldProjection(
        mergedVarProjection,
        fieldProjections.stream().anyMatch(OpInputFieldProjection::required),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputFieldProjection that = (OpInputFieldProjection) o;
    return required == that.required;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required);
  }
}
