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
public class OpReferenceContext
    extends ReferenceContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> {

  public OpReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<OpEntityProjection, OpModelProjection<?, ?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull OpEntityProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {
    return new OpEntityProjection(type, location);
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new OpRecordModelProjection(type, location);
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new OpMapModelProjection(type, location);
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new OpListModelProjection(type, location);
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new OpPrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull OpEntityProjection toSelfVar(final @NotNull OpModelProjection<?, ?, ?, ?> mRef) {
    assert mRef.isResolved();
    OpEntityProjection ep = (OpEntityProjection) mRef.entityProjection();
    if (ep == null) {
      final DatumTypeApi modelType = mRef.type();
      return new OpEntityProjection(
          modelType,
          mRef.flag(),
          ProjectionUtils.singletonLinkedHashMap(
              modelType.self().name(),
              new OpTagProjectionEntry(
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
  protected @NotNull OpModelProjection<?, ?, ?, ?> fromSelfVar(final @NotNull OpEntityProjection eRef) {
    OpModelProjection<?, ?, ?, ?> res = super.fromSelfVar(eRef);
    assert res.flag() == eRef.flag();
    return res;
  }
}
