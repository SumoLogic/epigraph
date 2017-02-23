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

package ws.epigraph.projections.req.delete;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.projections.req.ReqParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteFieldProjection extends AbstractReqFieldProjection<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?, ?>,
    ReqDeleteFieldProjection
    > {

  public ReqDeleteFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqDeleteVarProjection projection,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
  }

  @Override
  public @NotNull ReqDeleteFieldProjection setVarProjection(final @NotNull ReqDeleteVarProjection varProjection) {
    return new ReqDeleteFieldProjection(varProjection, TextLocation.UNKNOWN);
  }
}
