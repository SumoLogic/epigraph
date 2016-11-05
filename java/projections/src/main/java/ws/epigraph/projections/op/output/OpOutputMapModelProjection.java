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

package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputMapModelProjection
    extends OpOutputModelProjection<OpOutputMapModelProjection, MapType>
    implements GenMapModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputMapModelProjection,
    MapType
    > {

  @NotNull
  private final OpOutputVarProjection itemsProjection;
  @NotNull
  private final OpOutputKeyProjection keyProjection;

  public OpOutputMapModelProjection(
      @NotNull MapType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputMapModelProjection metaProjection,
      @NotNull OpOutputKeyProjection keyProjection,
      @NotNull OpOutputVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.itemsProjection = valuesProjection;
    this.keyProjection = keyProjection;
  }

  @NotNull
  public OpOutputVarProjection itemsProjection() { return itemsProjection; }

  @NotNull
  public OpOutputKeyProjection keyProjection() { return keyProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputMapModelProjection that = (OpOutputMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection) &&
           Objects.equals(keyProjection, that.keyProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection, keyProjection);
  }
}
