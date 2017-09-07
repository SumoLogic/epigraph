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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputReferenceContext
    extends ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>> {

  public OpOutputReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>> parent,
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
  protected OpOutputModelProjection<?, ?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new OpOutputRecordModelProjection(type, location);
  }

  @Override
  protected OpOutputModelProjection<?, ?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new OpOutputMapModelProjection(type, location);
  }

  @Override
  protected OpOutputModelProjection<?, ?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new OpOutputListModelProjection(type, location);
  }

  @Override
  protected OpOutputModelProjection<?, ?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new OpOutputPrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull OpOutputVarProjection toSelfVar(final @NotNull OpOutputModelProjection<?, ?, ?, ?> mRef) {
    assert mRef.isResolved();
    OpOutputVarProjection ep = (OpOutputVarProjection) mRef.entityProjection();
    if (ep == null) {
      final DatumTypeApi modelType = mRef.type();
      return new OpOutputVarProjection(
          modelType,
          mRef.flagged(),
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
    } else
      return ep;
  }

  @Override
  protected @NotNull OpOutputModelProjection<?, ?, ?, ?> fromSelfVar(final @NotNull OpOutputVarProjection eRef) {
    OpOutputModelProjection<?, ?, ?, ?> res = super.fromSelfVar(eRef);
    assert res.flagged() == eRef.flagged();
    return res;
  }
}
