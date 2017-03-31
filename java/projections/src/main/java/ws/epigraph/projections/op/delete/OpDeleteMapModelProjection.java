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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.MapTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteMapModelProjection
    extends OpDeleteModelProjection<OpDeleteModelProjection<?, ?, ?>, OpDeleteMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteMapModelProjection,
    MapTypeApi
    > {

  private /*final @NotNull*/ @Nullable OpDeleteVarProjection itemsProjection;
  private /*final @NotNull*/ @Nullable OpDeleteKeyProjection keyProjection;

  public OpDeleteMapModelProjection(
      @NotNull MapTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteKeyProjection keyProjection,
      @NotNull OpDeleteVarProjection valuesProjection,
      @Nullable List<OpDeleteMapModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, tails, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  public OpDeleteMapModelProjection(
      final @NotNull MapTypeApi model,
      final @NotNull TextLocation location) {
    super(model, location);
    itemsProjection = null;
    keyProjection = null;
  }

  @Override
  protected @NotNull ModelNormalizationContext<MapTypeApi, OpDeleteMapModelProjection> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> new OpDeleteMapModelProjection(m, TextLocation.UNKNOWN));
  }

  @Override
  public @NotNull OpDeleteVarProjection itemsProjection() {
    assert isResolved();
    assert itemsProjection != null;
    return itemsProjection;
  }

  public @NotNull OpDeleteKeyProjection keyProjection() {
    assert isResolved();
    assert keyProjection != null;
    return keyProjection;
  }

  @Override
  public void resolve(final ProjectionReferenceName name, final @NotNull OpDeleteMapModelProjection value) {
    super.resolve(name, value);
    this.itemsProjection = value.itemsProjection();
    this.keyProjection = value.keyProjection();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteMapModelProjection that = (OpDeleteMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
