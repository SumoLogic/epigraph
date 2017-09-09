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
import ws.epigraph.projections.op.OpProjectionTraversal;
import ws.epigraph.psi.PsiProcessingContext;

/**
 * Post-processor for output projections. Does a few things:
 * <ul>
 * <li>Checks that there are no default values</li>
 * <li>Checks that there are no default flags</li>
 * </ul>
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpOutputProjectionPostProcessor extends OpProjectionTraversal {
  private final @NotNull PsiProcessingContext context;

  OpOutputProjectionPostProcessor(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected boolean visitVarProjection(final @NotNull OpOutputVarProjection projection) {
    if (projection.flagged())
      context.addWarning("'required' flag is not supported on output projections, ignoring", projection.location());
    return super.visitVarProjection(projection);
  }

  @Override
  protected boolean visitModelProjection(final @NotNull OpOutputModelProjection<?, ?, ?, ?> projection) {
    if (projection.flagged())
      context.addWarning("'required' flag is not supported on output projections, ignoring", projection.location());
    return super.visitModelProjection(projection);
  }

}
