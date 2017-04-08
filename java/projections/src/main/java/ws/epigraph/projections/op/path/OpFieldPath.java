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
import ws.epigraph.projections.op.AbstractOpFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldPath extends AbstractOpFieldProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?, ?>,
    OpFieldPath
    > {

  public OpFieldPath(
//      @NotNull OpParams params,
//      @NotNull Annotations annotations,
      @NotNull OpVarPath projection,
      @NotNull TextLocation location) {
    super(/*params, annotations, */projection, location);
  }

  @Override
  protected OpFieldPath merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<OpFieldPath> fieldProjections,
      final @NotNull OpVarPath mergedVarProjection) {
    throw new RuntimeException("path polymorphic tails not supported");
  }

  @Override
  public @NotNull OpFieldPath setVarProjection(final @NotNull OpVarPath varProjection) {
    return new OpFieldPath(varProjection, TextLocation.UNKNOWN);
  }
}
