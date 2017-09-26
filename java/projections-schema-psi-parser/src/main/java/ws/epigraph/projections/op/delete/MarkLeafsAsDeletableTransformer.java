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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.op.OpProjectionTransformer;
import ws.epigraph.projections.op.output.OpOutputTagProjectionEntry;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class MarkLeafsAsDeletableTransformer extends OpProjectionTransformer {

  MarkLeafsAsDeletableTransformer(final PsiProcessingContext context) { }

  @Override
  protected OpOutputVarProjection transformVarProjection(
      final @NotNull OpOutputVarProjection varProjection,
      final @Nullable DataTypeApi dataType,
      final @NotNull Map<String, OpOutputTagProjectionEntry> transformedTagProjections,
      final @Nullable List<OpOutputVarProjection> transformedTails,
      final boolean mustRebuild) {

    boolean markDeletable = false;

    if (dataType != null) {
      TypeApi type = dataType.type();
      TagApi retroTag = dataType.retroTag();

      if (type.kind() == TypeKind.PRIMITIVE)
        markDeletable = true;
      else if (type.kind() == TypeKind.ENTITY && retroTag != null) {

        // questionable heuristic to keep things compatible when introducing retro tags:
        // mark entity projection is deletable if it's only tag is a retro tag

        OpOutputTagProjectionEntry singleTagProjection = varProjection.singleTagProjection();
        markDeletable = singleTagProjection != null && singleTagProjection.tag().equals(retroTag);
      }
    }

    if (!varProjection.flagged() && markDeletable) {
      OpOutputVarProjection newProjection = new OpOutputVarProjection(
          varProjection.type(),
          true,
          transformedTagProjections,
          varProjection.parenthesized(),
          transformedTails,
          varProjection.location()
      );

      fixTransformedEntity(varProjection, newProjection);
      return newProjection;
    } else return super.transformVarProjection(
        varProjection,
        dataType,
        transformedTagProjections,
        transformedTails,
        mustRebuild
    );
  }
}
