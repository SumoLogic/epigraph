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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GPrimitiveDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.types.PrimitiveTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPrimitiveModelProjection
    extends OpModelProjection<
    OpModelProjection<?, ?, ?, ?>,
    OpPrimitiveModelProjection,
    PrimitiveTypeApi,
    GPrimitiveDatum>
    implements GenPrimitiveModelProjection<
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    OpPrimitiveModelProjection,
    PrimitiveTypeApi> {

  public OpPrimitiveModelProjection(
      @NotNull PrimitiveTypeApi model,
      boolean flag,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpPrimitiveModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flag, defaultValue, params, annotations, metaProjection, tails, location);
  }

  public OpPrimitiveModelProjection(final @NotNull PrimitiveTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  protected OpPrimitiveModelProjection clone() {
    return new OpPrimitiveModelProjection(
        model,
        flag,
        defaultValue,
        params,
        annotations,
        metaProjection,
        polymorphicTails,
        location()
    );
  }

  @Override
  protected OpPrimitiveModelProjection merge(
      final @NotNull PrimitiveTypeApi model,
      final boolean mergedFlag,
      final @Nullable GPrimitiveDatum mergedDefault,
      final @NotNull List<OpPrimitiveModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpPrimitiveModelProjection> mergedTails) {

    return new OpPrimitiveModelProjection(
        model,
        mergedFlag,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

}
