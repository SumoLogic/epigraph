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

package ws.epigraph.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.output.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OutputProjectionPathRemover {
  private OutputProjectionPathRemover() {} // todo move somewhere

  public static PathRemovalResult removePath(@NotNull ReqOutputVarProjection projection, int steps)
      throws AmbiguousPathException {

    if (steps == 0) return new PathRemovalResult(projection, null);

    if (projection.tagProjections().size() > 1) throw new AmbiguousPathException();
    final @Nullable ReqOutputTagProjectionEntry tagProjection = projection.singleTagProjection();

    if (tagProjection == null) return PathRemovalResult.NULL;

    return removePath(tagProjection.projection(), steps - 1);
  }

  public static PathRemovalResult removePath(@NotNull ReqOutputModelProjection<?, ?, ?> projection, int steps)
      throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(null, projection);

    //noinspection SwitchStatementWithoutDefaultBranch,EnumSwitchStatementWhichMissesCases
    switch (projection.type().kind()) {

      case RECORD:
        ReqOutputRecordModelProjection rmp = (ReqOutputRecordModelProjection) projection;
        final @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections = rmp.fieldProjections();

        if (fieldProjections.size() > 1) throw new AmbiguousPathException();
        final @Nullable ReqOutputFieldProjectionEntry fieldProjection = rmp.pathFieldProjection();

        if (fieldProjection == null) return PathRemovalResult.NULL;

        return removePath(fieldProjection.fieldProjection().varProjection(), steps - 1);

      case MAP:
        ReqOutputMapModelProjection mmp = (ReqOutputMapModelProjection) projection;
        final @Nullable List<ReqOutputKeyProjection> keys = mmp.keys();
        if (keys == null) return PathRemovalResult.NULL;
        if (keys.size() != 1) throw new AmbiguousPathException();

        return removePath(mmp.itemsProjection(), steps - 1);

    }

    throw new AmbiguousPathException();
  }

  public static class PathRemovalResult {
    public static final PathRemovalResult NULL = new PathRemovalResult(null, null);

    private final @Nullable ReqOutputVarProjection varProjection;
    private final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection;

    PathRemovalResult(
        final @Nullable ReqOutputVarProjection varProjection,
        final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection) {

      this.varProjection = varProjection;
      this.modelProjection = modelProjection;
    }

    public @Nullable ReqOutputVarProjection varProjection() { return varProjection; }

    public @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection() { return modelProjection; }
  }
}
