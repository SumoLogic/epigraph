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

package ws.epigraph.projections.req.path;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFieldPath extends AbstractReqFieldProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?, ?>,
    ReqFieldPath
    > {

  public ReqFieldPath(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqVarPath projection,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
  }

  @Override
  protected ReqFieldPath merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<ReqFieldPath> fieldProjections,
      final @NotNull ReqVarPath mergedEntityProjection) {
    throw new RuntimeException("path polymorphic tails not supported");
  }

  @Override
  public @NotNull ReqFieldPath setEntityProjection(final @NotNull ReqVarPath varProjection) {
    return new ReqFieldPath(varProjection, TextLocation.UNKNOWN);
  }
}
