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
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpRecordModelProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqProjectionTraversal;
import ws.epigraph.projections.req.ReqRecordModelProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.TagApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AbstractReqTraversal extends ReqProjectionTraversal {
  protected final @NotNull PsiProcessingContext context;

  public AbstractReqTraversal(final @NotNull PsiProcessingContext context) {this.context = context;}

  @Override
  protected void registerMissingGuideTag(
      final @NotNull ReqEntityProjection projection,
      final @NotNull OpEntityProjection projection2,
      final @NotNull TagApi tag) {

    //noinspection ConstantConditions
    context.addError(
        String.format("Tag '%s' is not supported", tag.name()),
        projection.tagProjection(tag.name()).location()
    );
  }

  @Override
  protected void registerMissingGuideTail(
      final @NotNull ReqEntityProjection projection,
      final @NotNull OpEntityProjection projection2,
      final @NotNull ReqEntityProjection tail) {

    context.addError("Tail is not supported", tail.location());
  }

  @Override
  protected void registerMissingGuideField(
      final @NotNull ReqRecordModelProjection mp,
      final @NotNull OpRecordModelProjection gmp,
      final @NotNull String fieldName) {

    //noinspection ConstantConditions
    context.addError(String.format("Field '%s' is not supported", fieldName), mp.fieldProjection(fieldName).location());
  }
}
