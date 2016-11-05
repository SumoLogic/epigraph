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

package ws.epigraph.projections.op.input;

import ws.epigraph.data.MapDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputMapModelProjection
    extends OpInputModelProjection<OpInputMapModelProjection, MapType, MapDatum>
    implements GenMapModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    OpInputMapModelProjection,
    MapType
    > {
  @NotNull
  private final OpInputVarProjection valuesProjection;

  public OpInputMapModelProjection(
      @NotNull MapType model,
      boolean required,
      @Nullable MapDatum defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputMapModelProjection metaProjection,
      @NotNull OpInputVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, required, defaultValue, annotations, metaProjection, location);
    this.valuesProjection = valuesProjection;
  }

  @NotNull
  public OpInputVarProjection itemsProjection() { return valuesProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputMapModelProjection that = (OpInputMapModelProjection) o;
    return Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), valuesProjection);
  }
}
