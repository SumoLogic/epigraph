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
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputReferenceContext
    extends ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>> {

  public OpOutputReferenceContext(
      final @NotNull Qn referencesNamespace,
      final @Nullable ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull OpOutputVarProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new OpOutputVarProjection(type, location);
  }

  @Override
  protected @NotNull OpOutputVarProjection toSelfVar(final @NotNull OpOutputModelProjection<?, ?, ?> mRef) {
    final DatumTypeApi modelType = mRef.type();
    return new OpOutputVarProjection(
        modelType,
        ProjectionUtils.singletonLinkedHashMap(
            modelType.self().name(),
            new OpOutputTagProjectionEntry(
                modelType.self(),
                mRef,
                TextLocation.UNKNOWN
            )
        ),
        false,
        null,
        TextLocation.UNKNOWN
    );
  }
}
