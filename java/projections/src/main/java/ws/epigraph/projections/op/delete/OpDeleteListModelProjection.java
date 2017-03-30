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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.ListTypeApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteListModelProjection
    extends OpDeleteModelProjection<OpDeleteModelProjection<?, ?, ?>, OpDeleteListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteListModelProjection,
    ListTypeApi
    > {

  private /*final*/ @Nullable OpDeleteVarProjection itemsProjection;

  public OpDeleteListModelProjection(
      @NotNull ListTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteVarProjection itemsProjection,
      @Nullable List<OpDeleteListModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, tails, location);
    this.itemsProjection = itemsProjection;
  }

  public OpDeleteListModelProjection(final @NotNull ListTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.itemsProjection = null;
  }

  @Override
  public @NotNull OpDeleteListModelProjection newReference(
      final @NotNull ListTypeApi type,
      final @NotNull TextLocation location) {
    return new OpDeleteListModelProjection(type, location);
  }

  @Override
  public @NotNull OpDeleteVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  @Override
  public void resolve(final @NotNull Qn name, final @NotNull OpDeleteListModelProjection value) {
    super.resolve(name, value);
    this.itemsProjection = value.itemsProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteListModelProjection that = (OpDeleteListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
