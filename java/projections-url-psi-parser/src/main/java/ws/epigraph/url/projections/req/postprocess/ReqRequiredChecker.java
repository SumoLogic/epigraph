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
import ws.epigraph.projections.op.output.OpOutputFieldProjectionEntry;
import ws.epigraph.projections.op.output.OpOutputRecordModelProjection;
import ws.epigraph.projections.op.output.OpOutputTagProjectionEntry;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqRecordModelProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.url.projections.req.AbstractReqTraversal;

import java.util.Map;

/**
 * Checks that all parts required by the op projection are present in the req projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqRequiredChecker extends AbstractReqTraversal {
  public ReqRequiredChecker(final @NotNull PsiProcessingContext context) {
    super(context);
  }

  @Override
  protected boolean visitVarProjection(
      final @NotNull ReqEntityProjection projection,
      final @NotNull OpOutputVarProjection guide) {

    for (final Map.Entry<String, OpOutputTagProjectionEntry> entry : guide.tagProjections().entrySet()) {
      String tagName = entry.getKey();
      OpOutputTagProjectionEntry gtpe = entry.getValue();

      if (projection.tagProjection(tagName) == null && gtpe.projection().flagged()) {
        context.addError(String.format("Required tag '%s' is missing", tagName), projection.location());
      }
    }

    // todo required on tails? (and model tails too)

    return super.visitVarProjection(projection, guide);
  }

  @Override
  protected boolean visitRecordModelProjection(
      final @NotNull ReqRecordModelProjection projection,
      final @NotNull OpOutputRecordModelProjection guide) {

    for (final Map.Entry<String, OpOutputFieldProjectionEntry> entry : guide.fieldProjections().entrySet()) {
      String fieldName = entry.getKey();
      OpOutputFieldProjectionEntry gfpe = entry.getValue();

      if (projection.fieldProjection(fieldName) == null && gfpe.fieldProjection().flagged()) {
        context.addError(String.format("Required field '%s' is missing", fieldName), projection.location());
      }
    }

    return super.visitRecordModelProjection(projection, guide);
  }
}
