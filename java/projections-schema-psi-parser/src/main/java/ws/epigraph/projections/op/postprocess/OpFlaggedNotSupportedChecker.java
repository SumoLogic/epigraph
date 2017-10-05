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

package ws.epigraph.projections.op.postprocess;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.op.OpProjectionTraversal;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.psi.PsiProcessingContext;

/**
 * Checks for flag presence and reports as 'flagged not supported' if found
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpFlaggedNotSupportedChecker extends OpProjectionTraversal {
  private final @NotNull PsiProcessingContext context;

  // keep in sync with ReqFlaggedNotSupportedChecker

  public OpFlaggedNotSupportedChecker(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected boolean visitVarProjection(final @NotNull OpEntityProjection projection) {
    if (projection.flag())
      context.addWarning("flag is not supported on output projections, ignoring", projection.location());
    return super.visitVarProjection(projection);
  }

  @Override
  protected boolean visitModelProjection(final @NotNull OpModelProjection<?, ?, ?, ?> projection) {
    if (projection.flag())
      context.addWarning("flag is not supported on output projections, ignoring", projection.location());
    return super.visitModelProjection(projection);
  }

}
