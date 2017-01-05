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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpInputModelProjection<
    MP extends OpInputModelProjection</*MP*/?, ?, D>,
    M extends DatumTypeApi,
    D extends GDatum>
    extends AbstractOpModelProjection<MP, M> {

  protected final boolean required;
  protected final @Nullable D defaultValue;

  protected OpInputModelProjection(
      @NotNull M model,
      boolean required,
      @Nullable D defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @NotNull TextLocation location
  ) {
    super(model, metaProjection, params, annotations, location);
    this.required = required;
    this.defaultValue = defaultValue;
  }

  public boolean required() { return required; }

  public @Nullable D defaultValue() { return defaultValue; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputModelProjection<?, ?, ?> that = (OpInputModelProjection<?, ?, ?>) o;
    return required == that.required && Objects.equals(defaultValue, that.defaultValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, defaultValue);
  }
}
