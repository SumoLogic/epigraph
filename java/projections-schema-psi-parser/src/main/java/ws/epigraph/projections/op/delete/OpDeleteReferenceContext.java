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
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteReferenceContext
    extends ReferenceContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>> {

  public OpDeleteReferenceContext(
      final @NotNull Qn referencesNamespace,
      final @Nullable ReferenceContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull OpDeleteVarProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {

    return new OpDeleteVarProjection(type, location);
  }

  @Override
  protected OpDeleteModelProjection<?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new OpDeleteRecordModelProjection(type, location);
  }

  @Override
  protected OpDeleteModelProjection<?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new OpDeleteMapModelProjection(type, location);
  }

  @Override
  protected OpDeleteModelProjection<?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new OpDeleteListModelProjection(type, location);
  }

  @Override
  protected OpDeleteModelProjection<?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new OpDeletePrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull OpDeleteVarProjection toSelfVar(final @NotNull OpDeleteModelProjection<?, ?, ?> mRef) {
    final DatumTypeApi modelType = mRef.type();
    return new OpDeleteVarProjection(
        modelType,
        false, // ???
        ProjectionUtils.singletonLinkedHashMap(
            modelType.self().name(),
            new OpDeleteTagProjectionEntry(
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
