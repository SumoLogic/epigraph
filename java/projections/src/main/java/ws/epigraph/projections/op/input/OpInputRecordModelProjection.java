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

package ws.epigraph.projections.op.input;

import ws.epigraph.data.RecordDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputRecordModelProjection
    extends OpInputModelProjection<OpInputRecordModelProjection, RecordType, RecordDatum>
    implements GenRecordModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    RecordType
    > {

  private static final ThreadLocal<IdentityHashMap<OpInputRecordModelProjection, OpInputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @NotNull
  private Map<String, OpInputFieldProjectionEntry> fieldProjections;

  public OpInputRecordModelProjection(
      @NotNull RecordType model,
      boolean required,
      @Nullable RecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputRecordModelProjection metaProjection,
      @NotNull Map<String, OpInputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {

    super(model, required, defaultValue, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public static LinkedHashSet<OpInputFieldProjectionEntry> fields(OpInputFieldProjectionEntry... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @NotNull
  public Map<String, OpInputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  public void addFieldProjectionEntry(@NotNull OpInputFieldProjectionEntry fieldProjection) {
    fieldProjections.put(fieldProjection.field().name(), fieldProjection);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputRecordModelProjection that = (OpInputRecordModelProjection) o;

    IdentityHashMap<OpInputRecordModelProjection, OpInputRecordModelProjection> visitedMap = equalsVisited.get();
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
