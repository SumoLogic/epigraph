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

package ws.epigraph.projections.req.update;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.PrimitiveTypeApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdatePrimitiveModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdatePrimitiveModelProjection, PrimitiveTypeApi>
    implements GenPrimitiveModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdatePrimitiveModelProjection, PrimitiveTypeApi> {

  public ReqUpdatePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<ReqUpdatePrimitiveModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, true, params, annotations, tails, location);
  }

  public ReqUpdatePrimitiveModelProjection(
      final @NotNull PrimitiveTypeApi model,
      final @NotNull TextLocation location) {
    super(model, location);
  }

  @Override
  protected @NotNull ModelNormalizationContext<PrimitiveTypeApi, ReqUpdatePrimitiveModelProjection> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> new ReqUpdatePrimitiveModelProjection(m, TextLocation.UNKNOWN));
  }

}
