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

package ws.epigraph.projections.op.path;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.op.AbstractOpModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumTypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpModelPath<
    MP extends OpModelPath</*MP*/?, /*SMP*/?, ?>,
    SMP extends OpModelPath</*MP*/?, SMP, ?>,
    M extends DatumTypeApi
    > extends AbstractOpModelProjection<MP, SMP, M> {

  protected OpModelPath(
      @NotNull M model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull TextLocation location
  ) {
    super(model, null, params, annotations, null, location);
  }

  @Override
  protected @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext() {
    throw new RuntimeException("path references not supported");
  }
}
