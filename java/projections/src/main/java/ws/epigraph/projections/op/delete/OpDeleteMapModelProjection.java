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

package ws.epigraph.projections.op.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteMapModelProjection
    extends OpDeleteModelProjection<OpDeleteMapModelProjection, MapType>
    implements GenMapModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteMapModelProjection,
    MapType
    > {

  @NotNull
  private final OpDeleteVarProjection itemsProjection;
  @NotNull
  private final OpDeleteKeyProjection keyProjection;

  public OpDeleteMapModelProjection(
      @NotNull MapType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpDeleteKeyProjection keyProjection,
      @NotNull OpDeleteVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpDeleteVarProjection itemsProjection() { return itemsProjection; }

  @NotNull
  public OpDeleteKeyProjection keyProjection() { return keyProjection; }

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
