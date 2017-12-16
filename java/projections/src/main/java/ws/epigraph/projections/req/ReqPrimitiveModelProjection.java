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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.types.PrimitiveTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPrimitiveModelProjection
    extends ReqModelProjection<
    ReqModelProjection<?, ?, ?>,
    ReqPrimitiveModelProjection,
    PrimitiveTypeApi>
    implements GenPrimitiveModelProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqPrimitiveModelProjection,
    PrimitiveTypeApi> {

  public ReqPrimitiveModelProjection(
      @NotNull PrimitiveTypeApi model,
      boolean flag,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqPrimitiveModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flag, params, directives, metaProjection, tails, location);
  }

  public ReqPrimitiveModelProjection(final @NotNull PrimitiveTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public static @NotNull ReqPrimitiveModelProjection pathEnd(
      @NotNull PrimitiveTypeApi model,
      @NotNull TextLocation location) {

    return new ReqPrimitiveModelProjection(
        model,
        false,
        ReqParams.EMPTY,
        Directives.EMPTY,
        null,
        null,
        location
    );
  }

  /* static */
  @Override
  protected ReqPrimitiveModelProjection merge(
      final @NotNull PrimitiveTypeApi model,
      final boolean mergedFlag,
      final @NotNull List<ReqPrimitiveModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqPrimitiveModelProjection> mergedTails) {

    return new ReqPrimitiveModelProjection(
        model,
        mergedFlag,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }
}
