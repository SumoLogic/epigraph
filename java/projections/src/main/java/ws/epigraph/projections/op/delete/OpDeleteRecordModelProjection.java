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

package ws.epigraph.projections.op.delete;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteRecordModelProjection
    extends OpDeleteModelProjection<OpDeleteRecordModelProjection, RecordType>
    implements GenRecordModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteRecordModelProjection,
    OpDeleteFieldProjectionEntry,
    OpDeleteFieldProjection,
    RecordType
    > {

  private static final
  ThreadLocal<IdentityHashMap<OpDeleteRecordModelProjection, OpDeleteRecordModelProjection>> equalsVisited =
      new ThreadLocal<>();

  @NotNull
  private Map<String, OpDeleteFieldProjectionEntry> fieldProjections;

  public OpDeleteRecordModelProjection(
      @NotNull RecordType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull Map<String, OpDeleteFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public Map<String, OpDeleteFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteRecordModelProjection that = (OpDeleteRecordModelProjection) o;

    IdentityHashMap<OpDeleteRecordModelProjection, OpDeleteRecordModelProjection> visitedMap = equalsVisited.get();
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
