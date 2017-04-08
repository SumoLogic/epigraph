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

package ws.epigraph.projections.req.input;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputFieldProjection extends AbstractReqFieldProjection<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?, ?>,
    ReqInputFieldProjection
    > {

  public ReqInputFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqInputVarProjection projection,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
  }

  @Override
  protected ReqInputFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<ReqInputFieldProjection> fieldProjections,
      final @NotNull ReqInputVarProjection mergedVarProjection) {
    return new ReqInputFieldProjection(mergedVarProjection, TextLocation.UNKNOWN);
  }

  @Override
  public @NotNull ReqInputFieldProjection setVarProjection(final @NotNull ReqInputVarProjection varProjection) {
    return new ReqInputFieldProjection(varProjection, TextLocation.UNKNOWN);
  }
}
