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

package ws.epigraph.url.projections.req.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.delete.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteReferenceContext
    extends ReferenceContext<ReqDeleteVarProjection, ReqDeleteModelProjection<?, ?, ?>> {

  public ReqDeleteReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<ReqDeleteVarProjection, ReqDeleteModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull ReqDeleteVarProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {

    return new ReqDeleteVarProjection(type, location);
  }

  @Override
  protected ReqDeleteModelProjection<?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new ReqDeleteRecordModelProjection(type, location);
  }

  @Override
  protected ReqDeleteModelProjection<?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new ReqDeleteMapModelProjection(type, location);
  }

  @Override
  protected ReqDeleteModelProjection<?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new ReqDeleteListModelProjection(type, location);
  }

  @Override
  protected ReqDeleteModelProjection<?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new ReqDeletePrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull ReqDeleteVarProjection toSelfVar(final @NotNull ReqDeleteModelProjection<?, ?, ?> mRef) {
    final DatumTypeApi modelType = mRef.type();
    return new ReqDeleteVarProjection(
        modelType,
        ProjectionUtils.singletonLinkedHashMap(
            modelType.self().name(),
            new ReqDeleteTagProjectionEntry(
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
