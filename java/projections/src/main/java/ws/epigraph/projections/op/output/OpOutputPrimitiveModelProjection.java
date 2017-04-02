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

package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.PrimitiveTypeApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection
    extends OpOutputModelProjection<OpOutputModelProjection<?, ?, ?>, OpOutputPrimitiveModelProjection, PrimitiveTypeApi>
    implements GenPrimitiveModelProjection<OpOutputModelProjection<?, ?, ?>, OpOutputPrimitiveModelProjection, PrimitiveTypeApi> {

  public OpOutputPrimitiveModelProjection(
      @NotNull PrimitiveTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<OpOutputPrimitiveModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, tails, location);
  }

  public OpOutputPrimitiveModelProjection(final @NotNull PrimitiveTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  protected @NotNull ModelNormalizationContext<PrimitiveTypeApi, OpOutputPrimitiveModelProjection> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> new OpOutputPrimitiveModelProjection(m, TextLocation.UNKNOWN));
  }

  @Override
  protected OpOutputPrimitiveModelProjection merge(
      final @NotNull PrimitiveTypeApi model,
      final @NotNull List<OpOutputPrimitiveModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpOutputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpOutputPrimitiveModelProjection> mergedTails, final boolean keepPhantomTails) {

    return new OpOutputPrimitiveModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

}
