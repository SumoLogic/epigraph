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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqReferenceContext
    extends ReferenceContext<ReqEntityProjection, ReqModelProjection<?, ?, ?>> {

  public ReqReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<ReqEntityProjection, ReqModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull ReqEntityProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {

    return new ReqEntityProjection(type, location);
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new ReqRecordModelProjection(type, location);
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new ReqMapModelProjection(type, location);
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new ReqListModelProjection(type, location);
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new ReqPrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull ReqEntityProjection toSelfVar(final @NotNull ReqModelProjection<?, ?, ?> mRef) {
    assert mRef.isResolved();
    ReqEntityProjection ep = (ReqEntityProjection) mRef.entityProjection();
    if (ep == null) {
      final DatumTypeApi modelType = mRef.type();
      return new ReqEntityProjection(
          modelType,
          mRef.flag(),
          ProjectionUtils.singletonLinkedHashMap(
              modelType.self().name(),
              new ReqTagProjectionEntry(
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
}
