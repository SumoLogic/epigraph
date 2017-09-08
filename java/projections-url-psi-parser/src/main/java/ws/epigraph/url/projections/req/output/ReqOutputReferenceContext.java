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

package ws.epigraph.url.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputReferenceContext
    extends ReferenceContext<ReqOutputVarProjection, ReqOutputModelProjection<?, ?, ?>> {

  public ReqOutputReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<ReqOutputVarProjection, ReqOutputModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull ReqOutputVarProjection newVarReference(
      final @NotNull TypeApi type,
      final @NotNull TextLocation location) {

    return new ReqOutputVarProjection(type, location);
  }

  @Override
  protected ReqOutputModelProjection<?, ?, ?> newRecordModelReference(
      final @NotNull RecordTypeApi type, final @NotNull TextLocation location) {
    return new ReqOutputRecordModelProjection(type, location);
  }

  @Override
  protected ReqOutputModelProjection<?, ?, ?> newMapModelReference(
      final @NotNull MapTypeApi type, final @NotNull TextLocation location) {
    return new ReqOutputMapModelProjection(type, location);
  }

  @Override
  protected ReqOutputModelProjection<?, ?, ?> newListModelReference(
      final @NotNull ListTypeApi type, final @NotNull TextLocation location) {
    return new ReqOutputListModelProjection(type, location);
  }

  @Override
  protected ReqOutputModelProjection<?, ?, ?> newPrimitiveModelReference(
      final @NotNull PrimitiveTypeApi type, final @NotNull TextLocation location) {
    return new ReqOutputPrimitiveModelProjection(type, location);
  }

  @Override
  protected @NotNull ReqOutputVarProjection toSelfVar(final @NotNull ReqOutputModelProjection<?, ?, ?> mRef) {
    assert mRef.isResolved();
    ReqOutputVarProjection ep = (ReqOutputVarProjection) mRef.entityProjection();
    if (ep == null) {
      final DatumTypeApi modelType = mRef.type();
      return new ReqOutputVarProjection(
          modelType,
          mRef.flagged(),
          ProjectionUtils.singletonLinkedHashMap(
              modelType.self().name(),
              new ReqOutputTagProjectionEntry(
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
