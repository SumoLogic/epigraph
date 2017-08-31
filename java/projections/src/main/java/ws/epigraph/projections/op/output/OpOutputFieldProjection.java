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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.AbstractOpFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection extends AbstractOpFieldProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputFieldProjection
    > {

  // private final boolean flagged; // flagged field = flagged field retro model | var = all models flagged

  public OpOutputFieldProjection(
//      @NotNull OpParams params,
//      @NotNull Annotations annotations,
      @NotNull OpOutputVarProjection projection,
      @NotNull TextLocation location) {
    super(/*params, annotations, */projection, location);
  }

  @Override
  public @NotNull OpOutputFieldProjection setVarProjection(final @NotNull OpOutputVarProjection varProjection) {
    return new OpOutputFieldProjection(varProjection, TextLocation.UNKNOWN);
  }

  public boolean flagged() { return varProjection().flagged(); }

  @Override
  protected @NotNull OpOutputFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<OpOutputFieldProjection> fieldProjections,
//      final @NotNull OpParams mergedParams,
//      final @NotNull Annotations mergedAnnotations,
      final @NotNull OpOutputVarProjection mergedVarProjection) {

    return new OpOutputFieldProjection(
//        mergedParams,
//        mergedAnnotations,
        mergedVarProjection,
        TextLocation.UNKNOWN
    );
  }
}
