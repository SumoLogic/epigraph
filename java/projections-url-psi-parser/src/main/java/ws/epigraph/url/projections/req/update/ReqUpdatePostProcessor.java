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

package ws.epigraph.url.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqModelProjection;
import ws.epigraph.projections.req.ReqPrimitiveModelProjection;
import ws.epigraph.projections.req.ReqTagProjectionEntry;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeKind;
import ws.epigraph.url.projections.req.postprocess.ReqRequiredSynchronizer;

import java.util.List;

/**
 * A transformer/checker that will ensure correct 'update' flags on req projection:
 * for every leaf node: either it is flagged, or it has (exactly one) flagged parent
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdatePostProcessor extends ReqRequiredSynchronizer {
  private int flaggedInCurrentPath = 0;

  public ReqUpdatePostProcessor(final @NotNull MessagesContext context) { super(context); }

  @Override
  public void reset() {
    super.reset();
    flaggedInCurrentPath = 0;
  }

  @Override
  protected @NotNull ReqEntityProjection transformEntityProjection(
      final @NotNull ReqEntityProjection projection,
      final @Nullable DataTypeApi dataType) {

    ReqTagProjectionEntry singleTagProjection = projection.singleTagProjection();
    boolean selfModelFlagged = projection.type().kind() != TypeKind.ENTITY &&
                               singleTagProjection != null &&
                               singleTagProjection.projection().flag();

    int flagged = projection.flag() && !selfModelFlagged ? 1 : 0;
    flaggedInCurrentPath += flagged;

    if (flaggedInCurrentPath > 1)
      context.addError("'replace' flags cannot be nested", projection.location());

    try {
      return super.transformEntityProjection(projection, dataType);
    } finally {
      flaggedInCurrentPath -= flagged;
    }
  }

  @Override
  protected @NotNull ReqModelProjection<?, ?, ?> transformModelProjection(final @NotNull ReqModelProjection<?, ?, ?> projection) {
    int flagged = projection.flag() ? 1 : 0;
    flaggedInCurrentPath += flagged;

    if (flaggedInCurrentPath > 1)
      context.addError("'replace' flags cannot be nested", projection.location());

    try {
      return super.transformModelProjection(projection);
    } finally {
      flaggedInCurrentPath -= flagged;
    }

  }

  @Override
  protected @NotNull ReqPrimitiveModelProjection transformPrimitiveProjection(
      final @NotNull ReqPrimitiveModelProjection primitiveModelProjection,
      final @Nullable List<ReqPrimitiveModelProjection> transformedTails,
      final @Nullable ReqModelProjection<?, ?, ?> transformedMeta,
      final boolean mustRebuild) {

    if (flaggedInCurrentPath == 0 || flagModel(primitiveModelProjection)) {
      ReqPrimitiveModelProjection newProjection = new ReqPrimitiveModelProjection(
          primitiveModelProjection.type(),
          true,
          primitiveModelProjection.params(),
          primitiveModelProjection.directives(),
          transformedMeta,
          transformedTails,
          primitiveModelProjection.location()
      );

      fixTransformedModel(primitiveModelProjection, newProjection);
      return newProjection;
    } else {
      return super.transformPrimitiveProjection(
          primitiveModelProjection,
          transformedTails,
          transformedMeta,
          mustRebuild
      );
    }
  }
}
