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

package ws.epigraph.url.projections.req.postprocess;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqModelProjection;
import ws.epigraph.url.projections.req.AbstractReqTraversal;

/**
 * Checks that nothing is marked as flagged in the projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFlaggedNotSupportedChecker extends AbstractReqTraversal {
  public ReqFlaggedNotSupportedChecker(final @NotNull MessagesContext context) {
    super(context);
  }

  // keep in sync with OpFlaggedNotSupportedChecker

  @Override
  protected boolean visitVarProjection(
      final @NotNull ReqEntityProjection varProjection,
      final @NotNull OpEntityProjection guide) {

    if (varProjection.flag())
      context.addWarning("flag is not supported on output projections, ignoring", varProjection.location());
    return super.visitVarProjection(varProjection, guide);
  }

  @Override
  protected boolean visitModelProjection(
      final @NotNull ReqModelProjection<?, ?, ?> modelProjection,
      final @NotNull OpModelProjection<?, ?, ?, ?> guide) {

    if (modelProjection.flag())
      context.addWarning("flag is not supported on output projections, ignoring", modelProjection.location());
    return super.visitModelProjection(modelProjection, guide);
  }
}
