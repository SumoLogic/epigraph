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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateFieldProjection extends AbstractReqFieldProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateFieldProjection
    > {

  public ReqUpdateFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqUpdateVarProjection projection,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
  }

  @Override
  protected ReqUpdateFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<ReqUpdateFieldProjection> fieldProjections,
      final @NotNull ReqUpdateVarProjection mergedVarProjection) {

    return new ReqUpdateFieldProjection(mergedVarProjection, TextLocation.UNKNOWN);
  }

  @Override
  public @NotNull ReqUpdateFieldProjection setVarProjection(final @NotNull ReqUpdateVarProjection varProjection) {
    return new ReqUpdateFieldProjection(varProjection, TextLocation.UNKNOWN);
  }
}
