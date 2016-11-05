/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.req.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteRecordModelProjection
    extends ReqDeleteModelProjection<ReqDeleteRecordModelProjection, RecordType>
    implements GenRecordModelProjection<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>,
    ReqDeleteRecordModelProjection,
    ReqDeleteFieldProjectionEntry,
    ReqDeleteFieldProjection,
    RecordType
    > {
  private static final ThreadLocal<IdentityHashMap<ReqDeleteRecordModelProjection, ReqDeleteRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @NotNull
  private Map<String, ReqDeleteFieldProjectionEntry> fieldProjections;

  public ReqDeleteRecordModelProjection(
      @NotNull RecordType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull Map<String, ReqDeleteFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public Map<String, ReqDeleteFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Nullable
  public ReqDeleteFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqDeleteRecordModelProjection that = (ReqDeleteRecordModelProjection) o;

    IdentityHashMap<ReqDeleteRecordModelProjection, ReqDeleteRecordModelProjection> visitedMap = equalsVisited.get();
    boolean mapWasNull = visitedMap == null;
    if (mapWasNull) {
      visitedMap = new IdentityHashMap<>();
      equalsVisited.set(visitedMap);
    } else {
      if (that == visitedMap.get(this)) return true;
      if (visitedMap.containsKey(this)) return false;
    }
    visitedMap.put(this, that);
    boolean res = Objects.equals(fieldProjections, that.fieldProjections);
    if (mapWasNull) equalsVisited.remove();
    return res;
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
