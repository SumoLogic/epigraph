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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFieldProjection extends AbstractFieldProjection <
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    OpFieldProjection
    > {

  // private final boolean flag; // flag field = flag field retro model | var = all models flag

  public OpFieldProjection(
//      @NotNull OpParams params,
//      @NotNull Annotations annotations,
      @NotNull OpEntityProjection projection,
      @NotNull TextLocation location) {
    super(/*params, annotations, */projection, location);
  }

  @Override
  public @NotNull OpFieldProjection setProjection(final @NotNull OpEntityProjection varProjection) {
    return new OpFieldProjection(varProjection, TextLocation.UNKNOWN);
  }

  public boolean flag() { return projection().flag(); }

  @Override
  protected @NotNull OpFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<OpFieldProjection> fieldProjections,
//      final @NotNull OpParams mergedParams,
//      final @NotNull Annotations mergedAnnotations,
      final @NotNull OpEntityProjection mergedEntityProjection) {

    return new OpFieldProjection(
//        mergedParams,
//        mergedAnnotations,
        mergedEntityProjection,
        TextLocation.UNKNOWN
    );
  }
}
