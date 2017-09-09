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

package ws.epigraph.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.PrimitiveTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputPrimitiveModelProjection
    extends ReqOutputModelProjection<ReqOutputModelProjection<?, ?, ?>, ReqOutputPrimitiveModelProjection, PrimitiveTypeApi>
    implements GenPrimitiveModelProjection<ReqOutputModelProjection<?, ?, ?>, ReqOutputPrimitiveModelProjection, PrimitiveTypeApi> {

  public ReqOutputPrimitiveModelProjection(
      @NotNull PrimitiveTypeApi model,
      boolean flagged,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqOutputModelProjection<?, ?, ?> metaProjection,
      @Nullable List<ReqOutputPrimitiveModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, flagged, params, directives, metaProjection, tails, location);
  }

  public ReqOutputPrimitiveModelProjection(
      final @NotNull PrimitiveTypeApi model,
      final @NotNull TextLocation location) {
    super(model, location);
  }

  /* static */
  @Override
  protected ReqOutputPrimitiveModelProjection merge(
      final @NotNull PrimitiveTypeApi model,
      final boolean mergedFlagged,
      final @NotNull List<ReqOutputPrimitiveModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqOutputModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqOutputPrimitiveModelProjection> mergedTails) {

    return new ReqOutputPrimitiveModelProjection(
        model,
        mergedFlagged,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }
}
