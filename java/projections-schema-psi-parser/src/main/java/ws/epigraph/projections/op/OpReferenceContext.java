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
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpReferenceContext
    extends ReferenceContext<OpProjection<?, ?>, OpEntityProjection, OpModelProjection<?, ?, ?, ?>> {

  public OpReferenceContext(
      final @NotNull ProjectionReferenceName referencesNamespace,
      final @Nullable ReferenceContext<OpProjection<?, ?>, OpEntityProjection, OpModelProjection<?, ?, ?, ?>> parent,
      final @NotNull PsiProcessingContext context) {
    super(referencesNamespace, parent, context);
  }

  @Override
  protected @NotNull OpEntityProjection newEntityReference(
      @NotNull TypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    OpEntityProjection projection = new OpEntityProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newRecordModelReference(
      @NotNull RecordTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    OpRecordModelProjection projection = new OpRecordModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newMapModelReference(
      @NotNull MapTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    OpMapModelProjection projection = new OpMapModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newListModelReference(
      @NotNull ListTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    OpListModelProjection projection = new OpListModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }

  @Override
  protected OpModelProjection<?, ?, ?, ?> newPrimitiveModelReference(
      @NotNull PrimitiveTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location) {

    OpPrimitiveModelProjection projection = new OpPrimitiveModelProjection(type, location);
    if (name != null)
      projection.setReferenceName(name);
    return projection;
  }
}
