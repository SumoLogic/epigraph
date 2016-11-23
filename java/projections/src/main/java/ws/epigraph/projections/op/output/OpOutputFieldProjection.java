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

package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpFieldProjection;
import ws.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.types.DataType;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputFieldProjection extends AbstractOpFieldProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputFieldProjection
    > {

  public OpOutputFieldProjection(
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull OpOutputVarProjection projection,
      @NotNull TextLocation location) {
    super(params, annotations, projection, location);
  }

  @NotNull
  @Override
  protected OpOutputFieldProjection merge(
      @NotNull final DataType type,
      @NotNull final List<OpOutputFieldProjection> fieldProjections,
      @NotNull final OpParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @NotNull final OpOutputVarProjection mergedVarProjection) {

    return new OpOutputFieldProjection(
        mergedParams,
        mergedAnnotations,
        mergedVarProjection,
        TextLocation.UNKNOWN
    );
  }
}
