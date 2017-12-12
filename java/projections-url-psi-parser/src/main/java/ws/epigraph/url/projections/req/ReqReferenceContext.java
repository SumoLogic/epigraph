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
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqReferenceContext
    extends ReferenceContext<ReqProjection<?, ?>, ReqEntityProjection, ReqModelProjection<?, ?, ?>> {

  public ReqReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<ReqProjection<?, ?>, ReqEntityProjection, ReqModelProjection<?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {

    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull ReqEntityProjection newEntityReference(
      @NotNull TypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    ReqEntityProjection projection = new ReqEntityProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newRecordModelReference(
      @NotNull RecordTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    ReqRecordModelProjection projection = new ReqRecordModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newMapModelReference(
      @NotNull MapTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    ReqMapModelProjection projection = new ReqMapModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newListModelReference(
      @NotNull ListTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    ReqListModelProjection projection = new ReqListModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected ReqModelProjection<?, ?, ?> newPrimitiveModelReference(
      @NotNull PrimitiveTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    ReqPrimitiveModelProjection projection = new ReqPrimitiveModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

}
