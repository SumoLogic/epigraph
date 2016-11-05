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

import ws.epigraph.data.PrimitiveDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputPrimitiveModelProjection
    extends OpInputModelProjection<OpInputPrimitiveModelProjection, PrimitiveType<?>, PrimitiveDatum<?>>
    implements GenPrimitiveModelProjection<OpInputPrimitiveModelProjection, PrimitiveType<?>> {

  public OpInputPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      boolean required,
      @Nullable PrimitiveDatum<?> defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, required, defaultValue, annotations, metaProjection, location);
  }
}
